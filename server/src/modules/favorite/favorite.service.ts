import {
  Injectable,
  ConflictException,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class FavoriteService {
  constructor(private prisma: PrismaService) {}

  async addFavorite(userId: string, courseId: string) {
    // Kiểm tra khóa học tồn tại
    const course = await this.prisma.course.findUnique({
      where: { id: courseId },
    });
    if (!course) {
      throw new NotFoundException('Course not found');
    }

    // Kiểm tra đã favorite chưa
    const existing = await this.prisma.favorite.findUnique({
      where: {
        userId_courseId: {
          userId,
          courseId,
        },
      },
    });
    if (existing) {
      throw new ConflictException('Course already in favorites');
    }

    return this.prisma.favorite.create({
      data: {
        userId,
        courseId,
      },
      include: {
        course: true,
      },
    });
  }

  async removeFavorite(userId: string, courseId: string) {
    const favorite = await this.prisma.favorite.findUnique({
      where: {
        userId_courseId: {
          userId,
          courseId,
        },
      },
    });
    if (!favorite) {
      throw new NotFoundException('Favorite not found');
    }

    return this.prisma.favorite.delete({
      where: {
        userId_courseId: {
          userId,
          courseId,
        },
      },
    });
  }

  async getFavorites(userId: string) {
    return this.prisma.favorite.findMany({
      where: { userId },
      include: {
        course: {
          include: {
            instructor: {
              select: {
                id: true,
                fullName: true,
              },
            },
            category: true,
          },
        },
      },
      orderBy: {
        createdAt: 'desc',
      },
    });
  }
}
