import {
  ForbiddenException,
  Injectable,
  NotFoundException,
  BadRequestException,
  InternalServerErrorException,
} from '@nestjs/common';
import { Role } from '@prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { CreateLessonDto } from './dto/create-lesson.dto';
import { UpdateLessonDto } from './dto/update-lesson.dto';
import { createClient, SupabaseClient } from '@supabase/supabase-js';

@Injectable()
export class LessonsService {
  private supabase: SupabaseClient;

  constructor(private readonly prisma: PrismaService) {
    this.supabase = createClient(
      process.env.SUPABASE_URL || '',
      process.env.SUPABASE_SERVICE_ROLE_KEY || '',
    );
  }

  async findOne(id: string, user: { userId: string; role: Role }) {
    const lesson = await this.prisma.lesson.findUnique({
      where: { id },
      include: {
        chapter: {
          include: {
            course: true,
          },
        },
        materials: true,
        quiz: {
          include: {
            questions: {
              include: {
                choices: true,
              },
            },
          },
        },
      },
    });

    if (!lesson || lesson.deletedAt) {
      throw new NotFoundException(`Lesson with id "${id}" not found`);
    }

    if (user.role === Role.STUDENT) {
      const isEnrolled = await this.prisma.enrollment.findUnique({
        where: {
          userId_courseId: {
            userId: user.userId,
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
      if (lesson.chapter.course.instructorId !== user.userId) {
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

  async create(user: { userId: string; role: Role }, dto: CreateLessonDto) {
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
    user: { userId: string; role: Role },
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

  async remove(id: string, user: { userId: string; role: Role }) {
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

  private async assertChapterOwnership(
    chapterId: string,
    user: { userId: string; role: Role },
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
    user: { userId: string; role: Role },
  ) {
    if (user.role === Role.ADMIN) return;
    if (user.role !== Role.INSTRUCTOR || instructorId !== user.userId) {
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
    user: { userId: string; role: Role },
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
      if (lesson.chapter.course.instructorId !== user.userId) {
        throw new ForbiddenException(
          'You are not the instructor for this course',
        );
      }

      const fileExt = file.originalname.split('.').pop();
      const fileName = `${Date.now()}-${Math.floor(Math.random() * 10000)}.${fileExt}`;

      const filePath = `lesson_${lessonId}/${fileName}`;

      const { data: uploadData, error: uploadError } =
        await this.supabase.storage
          .from('materials')
          .upload(filePath, file.buffer, {
            contentType: file.mimetype,
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

      let materialType: 'VIDEO' | 'DOCUMENT' | 'LINK' | 'SOURCE_CODE' =
        'DOCUMENT';
      if (type.toUpperCase() === 'VIDEO') materialType = 'VIDEO';

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
      throw new InternalServerErrorException(
        'System error while processing material.',
      );
    }
  }
}
