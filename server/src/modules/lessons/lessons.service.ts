import {
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { Role } from '@prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { CreateLessonDto } from './dto/create-lesson.dto';
import { UpdateLessonDto } from './dto/update-lesson.dto';

@Injectable()
export class LessonsService {
  constructor(private readonly prisma: PrismaService) {}

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

    // Xóa mềm
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
}
