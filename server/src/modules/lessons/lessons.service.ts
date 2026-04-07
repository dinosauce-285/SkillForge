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
  private supabase: SupabaseClient; // Khai báo biến supabase cho class

  constructor(private readonly prisma: PrismaService) {
    // Lấy URL và Key từ biến môi trường (file .env)
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

    // Check authorization
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
      // Instructors can view if they own the course
      if (lesson.chapter.course.instructorId !== user.userId) {
        throw new ForbiddenException(
          'You are not the instructor for this course',
        );
      }
    }

    return lesson;
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

  async addMaterialToLesson(
    lessonId: string,
    title: string,
    type: string,
    file: Express.Multer.File,
  ) {
    if (!file) {
      throw new BadRequestException('Chưa đính kèm file!');
    }

    try {
      // 1. Tạo một cái tên file độc nhất (chống trùng lặp)
      const fileExt = file.originalname.split('.').pop();
      const fileName = `${Date.now()}-${Math.floor(Math.random() * 10000)}.${fileExt}`;

      const filePath = `lesson_${lessonId}/${fileName}`;

      // 2. Bắn buffer từ RAM thẳng lên Supabase Storage
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
          'Lỗi khi bắn file lên Supabase Storage.',
        );
      }

      // 3. Lấy Public URL
      const { data: publicUrlData } = this.supabase.storage
        .from('materials')
        .getPublicUrl(filePath);

      const fileUrl = publicUrlData.publicUrl;

      // 4. CHUẨN BỊ DỮ LIỆU KHỚP VỚI SCHEMA PRISMA CỦA BẠN

      // Chuyển đổi type từ Android ("Video", "Document") sang Enum Prisma
      let materialType: 'VIDEO' | 'DOCUMENT' | 'LINK' | 'SOURCE_CODE' =
        'DOCUMENT';
      if (type.toUpperCase() === 'VIDEO') materialType = 'VIDEO';

      // Cập nhật lại Tên Bài Học (title) vào bảng Lesson (vì UI Android có ô nhập Lesson Title)
      if (title && title.trim() !== '') {
        await this.prisma.lesson.update({
          where: { id: lessonId },
          data: { title: title.trim() },
        });
      }

      // 5. Lưu vào bảng LessonMaterial
      const newMaterial = await this.prisma.lessonMaterial.create({
        data: {
          lessonId: lessonId,
          type: materialType, // Dùng Enum
          fileUrl: fileUrl, // Dùng đúng tên cột fileUrl
          fileSize: file.size, // Truyền dung lượng file (tính bằng bytes)
          status: 'READY', // Upload xong thì set trạng thái READY luôn
        },
      });

      return {
        message: 'Upload tài liệu lên Supabase thành công mĩ mãn!',
        data: newMaterial,
      };
    } catch (error) {
      console.error(error);
      throw new InternalServerErrorException(
        'Lỗi hệ thống khi xử lý tài liệu.',
      );
    }
  }
}
