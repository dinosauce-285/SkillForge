import {
  ForbiddenException,
  Injectable,
  NotFoundException,
  BadRequestException,
  HttpException,
  InternalServerErrorException,
} from '@nestjs/common';
import { MaterialType, Role } from '@prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { CreateLessonDto } from './dto/create-lesson.dto';
import { UpdateLessonDto } from './dto/update-lesson.dto';
import { createClient, SupabaseClient } from '@supabase/supabase-js';

import ffmpeg from 'fluent-ffmpeg';
import * as ffmpegInstaller from '@ffmpeg-installer/ffmpeg';
import * as ffprobeInstaller from '@ffprobe-installer/ffprobe';
import * as fs from 'fs/promises';
import { file as tmpFile } from 'tmp-promise';

ffmpeg.setFfmpegPath(ffmpegInstaller.path);
ffmpeg.setFfprobePath(ffprobeInstaller.path);

@Injectable()
export class LessonsService {
  private supabase: SupabaseClient;
  private readonly maxMobileVideoBytes = 250 * 1024 * 1024;

  constructor(private readonly prisma: PrismaService) {
    this.supabase = createClient(
      process.env.SUPABASE_URL || '',
      process.env.SUPABASE_SERVICE_ROLE_KEY || '',
    );
  }

  async findOne(id: string, user: { id: string; role: Role }) {
    const lesson = await this.prisma.lesson.findUnique({
      where: { id },
      include: {
        chapter: {
          include: {
            course: true,
          },
        },
        materials: true,
      },
    });

    if (!lesson || lesson.deletedAt) {
      throw new NotFoundException(`Lesson with id "${id}" not found`);
    }

    if (user.role === Role.STUDENT) {
      const isEnrolled = await this.prisma.enrollment.findUnique({
        where: {
          userId_courseId: {
            userId: user.id,
            courseId: lesson.chapter.courseId,
          },
        },
      });

      if (!isEnrolled || isEnrolled.status !== 'ACTIVE') {
        if (!lesson.chapter.course.isFree) {
          throw new ForbiddenException(
            'You must be enrolled to view this lesson',
          );
        }
      }
    } else if (user.role === Role.INSTRUCTOR) {
      if (lesson.chapter.course.instructorId !== user.id) {
        throw new ForbiddenException(
          'You are not the instructor for this course',
        );
      }
    }

    const materials = await Promise.all(
      lesson.materials.map(async (material) => {
        const storagePath = this.extractMaterialsStoragePath(material.fileUrl);

        if (!storagePath) {
          return material;
        }

        const { data: signedData, error: signedError } =
          await this.supabase.storage
            .from('materials')
            .createSignedUrl(storagePath, 60 * 60);

        if (signedError || !signedData?.signedUrl) {
          return material;
        }

        return {
          ...material,
          fileUrl: signedData.signedUrl,
        };
      }),
    );

    return {
      ...lesson,
      materials,
    };
  }

  async create(user: { id: string; role: Role }, dto: CreateLessonDto) {
    await this.assertChapterOwnership(dto.chapterId, user);

    let orderIndex = dto.orderIndex;
    if (orderIndex === undefined) {
      const lastLesson = await this.prisma.lesson.findFirst({
        where: { chapterId: dto.chapterId, deletedAt: null },
        orderBy: { orderIndex: 'desc' },
      });
      orderIndex = lastLesson ? lastLesson.orderIndex + 1 : 0;
    }

    return this.prisma.lesson.create({
      data: {
        title: dto.title.trim(),
        chapterId: dto.chapterId,
        orderIndex,
      },
    });
  }

  async update(
    id: string,
    user: { id: string; role: Role },
    dto: UpdateLessonDto,
  ) {
    const lesson = await this.prisma.lesson.findUnique({
      where: { id },
      include: { chapter: { include: { course: true } } },
    });

    if (!lesson || lesson.deletedAt) {
      throw new NotFoundException(`Lesson with id "${id}" not found`);
    }

    this.assertCanManage(lesson.chapter.course.instructorId, user);

    return this.prisma.lesson.update({
      where: { id },
      data: {
        title: dto.title?.trim(),
        orderIndex: dto.orderIndex,
      },
    });
  }

  async remove(id: string, user: { id: string; role: Role }) {
    const lesson = await this.prisma.lesson.findUnique({
      where: { id },
      include: { chapter: { include: { course: true } } },
    });

    if (!lesson || lesson.deletedAt) {
      throw new NotFoundException(`Lesson with id "${id}" not found`);
    }

    this.assertCanManage(lesson.chapter.course.instructorId, user);

    return this.prisma.lesson.update({
      where: { id },
      data: { deletedAt: new Date() },
    });
  }

  async removeMaterial(materialId: string, user: { id: string; role: Role }) {
    const material = await this.prisma.lessonMaterial.findUnique({
      where: { id: materialId },
      include: {
        lesson: {
          include: {
            chapter: {
              include: {
                course: true,
              },
            },
          },
        },
      },
    });

    if (!material || material.lesson.deletedAt) {
      throw new NotFoundException(
        `Material with id "${materialId}" not found`,
      );
    }

    this.assertCanManage(material.lesson.chapter.course.instructorId, user);

    await this.prisma.lessonMaterial.delete({
      where: { id: materialId },
    });

    const storagePath = this.extractMaterialsStoragePath(material.fileUrl);
    const uploadedLessonPrefix = `lesson_${material.lessonId}/`;
    if (storagePath?.startsWith(uploadedLessonPrefix)) {
      const { error } = await this.supabase.storage
        .from('materials')
        .remove([storagePath]);

      if (error) {
        console.error('Supabase delete error:', error);
      }
    }

    return { message: 'Material deleted successfully' };
  }

  private async assertChapterOwnership(
    chapterId: string,
    user: { id: string; role: Role },
  ) {
    const chapter = await this.prisma.chapter.findUnique({
      where: { id: chapterId },
      include: { course: { select: { instructorId: true } } },
    });

    if (!chapter) throw new NotFoundException('Chapter not found');
    this.assertCanManage(chapter.course.instructorId, user);
  }

  private assertCanManage(
    instructorId: string,
    user: { id: string; role: Role },
  ) {
    if (user.role === Role.ADMIN) return;
    if (user.role !== Role.INSTRUCTOR || instructorId !== user.id) {
      throw new ForbiddenException(
        'You are not allowed to manage this content',
      );
    }
  }

  private extractMaterialsStoragePath(fileUrl: string): string | null {
    if (!fileUrl) {
      return null;
    }

    if (!fileUrl.startsWith('http')) {
      return fileUrl;
    }

    const markers = [
      '/storage/v1/object/public/materials/',
      '/storage/v1/object/sign/materials/',
      '/storage/v1/object/authenticated/materials/',
    ];

    for (const marker of markers) {
      const markerIndex = fileUrl.indexOf(marker);
      if (markerIndex === -1) {
        continue;
      }

      const pathWithQuery = fileUrl.substring(markerIndex + marker.length);
      const pathOnly = pathWithQuery.split('?')[0];
      return pathOnly || null;
    }

    return null;
  }

  async addMaterialToLesson(
    lessonId: string,
    type: string,
    file: Express.Multer.File,
    user: { id: string; role: Role },
  ) {
    if (!file) {
      throw new BadRequestException('No file attached!');
    }

    try {
      // Verify lesson exists and user is the instructor
      const lesson = await this.prisma.lesson.findUnique({
        where: { id: lessonId },
        include: {
          chapter: {
            include: {
              course: true,
            },
          },
        },
      });

      if (!lesson || lesson.deletedAt) {
        throw new NotFoundException(`Lesson with id "${lessonId}" not found`);
      }

      // Verify user is the course instructor
      if (lesson.chapter.course.instructorId !== user.id) {
        throw new ForbiddenException(
          'You are not the instructor for this course',
        );
      }

      let materialType: MaterialType = MaterialType.DOCUMENT;
      if (type.toUpperCase() === MaterialType.VIDEO) {
        materialType = MaterialType.VIDEO;
      }

      if (materialType === MaterialType.VIDEO) {
        const existingVideo = await this.prisma.lessonMaterial.findFirst({
          where: {
            lessonId,
            type: MaterialType.VIDEO,
          },
        });

        if (existingVideo) {
          throw new BadRequestException('Each lesson can only have one video.');
        }
      }

      let uploadBuffer = file.buffer;
      const fileExt = file.originalname.split('.').pop() || '';
      let fileName = `${Date.now()}-${Math.floor(Math.random() * 10000)}.${fileExt}`;
      let contentType = file.mimetype;

      if (materialType === MaterialType.VIDEO) {
        // Transcode video using ffmpeg
        const { path: inputPath, cleanup: cleanupInput } = await tmpFile({ postfix: '.' + fileExt });
        const { path: outputPath, cleanup: cleanupOutput } = await tmpFile({ postfix: '.mp4' });

        try {
          await fs.writeFile(inputPath, file.buffer);
          
          await new Promise<void>((resolve, reject) => {
            ffmpeg(inputPath)
              .outputOptions([
                "-vf scale=-2:'min(1080,ih)'",
                '-preset medium',
                '-crf 23',
                '-pix_fmt yuv420p',
                '-profile:v high',
                '-level 4.1',
                '-movflags +faststart',
              ])
              .videoCodec('libx264')
              .audioCodec('aac')
              .audioBitrate('128k')
              .format('mp4')
              .on('end', () => resolve())
              .on('error', (err) => reject(new InternalServerErrorException('Video encoding failed: ' + err.message)))
              .save(outputPath);
          });

          uploadBuffer = await fs.readFile(outputPath);
          contentType = 'video/mp4';
          fileName = `${Date.now()}-${Math.floor(Math.random() * 10000)}.mp4`;
        } finally {
          cleanupInput();
          cleanupOutput();
        }
      }

      const filePath = `lesson_${lessonId}/${fileName}`;

      const { data: uploadData, error: uploadError } =
        await this.supabase.storage
          .from('materials')
          .upload(filePath, uploadBuffer, {
            contentType: contentType,
            upsert: false,
          });

      if (uploadError) {
        console.error('Supabase Upload Error:', uploadError);
        throw new InternalServerErrorException(
          'Error uploading file to Supabase Storage.',
        );
      }

      const { data: publicUrlData } = this.supabase.storage
        .from('materials')
        .getPublicUrl(filePath);

      const fileUrl = publicUrlData.publicUrl;

      const newMaterial = await this.prisma.lessonMaterial.create({
        data: {
          lessonId: lessonId,
          type: materialType,
          fileUrl: fileUrl,
          fileSize: file.size,
          status: 'READY',
        },
      });

      return {
        message: 'Successfully uploaded material to Supabase!',
        data: newMaterial,
      };
    } catch (error) {
      console.error(error);
      if (error instanceof HttpException) {
        throw error;
      }
      throw new InternalServerErrorException(
        'System error while processing material.',
      );
    }
  }

  private assertMobilePlayableVideo(file: Express.Multer.File) {
    const extension = file.originalname.split('.').pop()?.toLowerCase();
    const isMp4 =
      extension === 'mp4' ||
      file.mimetype === 'video/mp4' ||
      file.mimetype === 'application/mp4';

    if (!isMp4) {
      throw new BadRequestException(
        'Lesson videos must be MP4 files for reliable mobile playback. Export as MP4 with H.264 video and AAC audio.',
      );
    }

    if (file.size > this.maxMobileVideoBytes) {
      throw new BadRequestException(
        'Video is too large for direct mobile streaming. Please upload an MP4 encoded for web/mobile, ideally 720p or 1080p H.264/AAC under 250MB.',
      );
    }
  }
}
