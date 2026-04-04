import {
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { Role } from '@prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { CreateChapterDto } from './dto/create-chapter.dto';
import { UpdateChapterDto } from './dto/update-chapter.dto';

@Injectable()
export class ChaptersService {
  constructor(private readonly prisma: PrismaService) {}

  async create(user: { userId: string; role: Role }, dto: CreateChapterDto) {
    await this.assertCourseOwnership(dto.courseId, user);

    let orderIndex = dto.orderIndex;
    // translated comment
    if (orderIndex === undefined) {
      const lastChapter = await this.prisma.chapter.findFirst({
        where: { courseId: dto.courseId, deletedAt: null },
        orderBy: { orderIndex: 'desc' },
      });
      orderIndex = lastChapter ? lastChapter.orderIndex + 1 : 0;
    }

    return this.prisma.chapter.create({
      data: {
        title: dto.title.trim(),
        courseId: dto.courseId,
        orderIndex,
      },
    });
  }

  async update(
    id: string,
    user: { userId: string; role: Role },
    dto: UpdateChapterDto,
  ) {
    const chapter = await this.prisma.chapter.findUnique({
      where: { id },
      include: { course: true },
    });

    if (!chapter || chapter.deletedAt) {
      throw new NotFoundException(`Chapter with id "${id}" not found`);
    }

    this.assertCanManage(chapter.course.instructorId, user);

    return this.prisma.chapter.update({
      where: { id },
      data: {
        title: dto.title?.trim(),
        orderIndex: dto.orderIndex,
      },
    });
  }

  async remove(id: string, user: { userId: string; role: Role }) {
    const chapter = await this.prisma.chapter.findUnique({
      where: { id },
      include: { course: true },
    });

    if (!chapter || chapter.deletedAt) {
      throw new NotFoundException(`Chapter with id "${id}" not found`);
    }

    this.assertCanManage(chapter.course.instructorId, user);

    // translated comment
    return this.prisma.chapter.update({
      where: { id },
      data: { deletedAt: new Date() },
    });
  }

  private async assertCourseOwnership(
    courseId: string,
    user: { userId: string; role: Role },
  ) {
    const course = await this.prisma.course.findUnique({
      where: { id: courseId },
      select: { instructorId: true },
    });

    if (!course) throw new NotFoundException('Course not found');
    this.assertCanManage(course.instructorId, user);
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
