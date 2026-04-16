import { Injectable, BadRequestException, ConflictException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateReviewDto } from './dto/create-review.dto';

@Injectable()
export class ReviewsService {
  constructor(private prisma: PrismaService) {}

  async createReview(studentId: string, courseId: string, dto: CreateReviewDto) {
    // 1. Ensure the student is enrolled in the course
    const enrollment = await this.prisma.enrollment.findUnique({
      where: {
        userId_courseId: { userId: studentId, courseId }
      }
    });

    if (!enrollment) {
      throw new BadRequestException('You can only review courses you are enrolled in.');
    }

    // 2. Check if a review already exists (upsert)
    const review = await this.prisma.review.upsert({
      where: {
        studentId_courseId: { studentId, courseId }
      },
      update: {
        rating: dto.rating,
        content: dto.content,
      },
      create: {
        studentId,
        courseId,
        rating: dto.rating,
        content: dto.content,
      }
    });

    return review;
  }

  async getCourseReviews(courseId: string) {
    const reviews = await this.prisma.review.findMany({
      where: { courseId },
      include: {
        student: {
          select: { fullName: true }
        }
      },
      orderBy: { createdAt: 'desc' }
    });

    // Calculate average rating dynamically
    const averageRating = reviews.length > 0
      ? reviews.reduce((acc, rev) => acc + rev.rating, 0) / reviews.length
      : 0;

    return {
      averageRating: parseFloat(averageRating.toFixed(1)),
      totalReviews: reviews.length,
      reviews
    };
  }
}