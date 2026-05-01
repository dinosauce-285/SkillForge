import { Injectable, NotFoundException, BadRequestException, ForbiddenException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateCouponDto } from './dto/create-coupon.dto';

@Injectable()
export class CouponsService {
  constructor(private prisma: PrismaService) {}

  async createCoupon(instructorId: string, createCouponDto: CreateCouponDto) {
    const existing = await this.prisma.coupon.findUnique({
      where: { code: createCouponDto.code }
    });

    if (existing) {
      throw new BadRequestException('Coupon code already exists');
    }

    return this.prisma.coupon.create({
      data: {
        code: createCouponDto.code.toUpperCase(),
        discountPercent: createCouponDto.discountPercent,
        isActive: createCouponDto.isActive,
        instructorId,
      }
    });
  }

  async getInstructorCoupons(instructorId: string) {
    return this.prisma.coupon.findMany({
      where: { instructorId },
      orderBy: { createdAt: 'desc' }
    });
  }

  async validateCoupon(code: string, courseId?: string) {
    const coupon = await this.prisma.coupon.findUnique({
      where: { code: code.toUpperCase() }
    });

    if (!coupon) {
      throw new NotFoundException('Coupon not found');
    }

    if (!coupon.isActive) {
      throw new BadRequestException('Coupon is inactive');
    }

    if (courseId) {
      const course = await this.prisma.course.findUnique({
        where: { id: courseId },
        select: { instructorId: true }
      });
      if (!course) {
        throw new NotFoundException('Course not found');
      }
      if (course.instructorId !== coupon.instructorId) {
        throw new BadRequestException('This coupon cannot be applied to this course');
      }
    }

    return {
      discountPercent: coupon.discountPercent,
      code: coupon.code,
    };
  }

  async deleteCoupon(instructorId: string, id: string) {
    const coupon = await this.prisma.coupon.findUnique({ where: { id } });
    if (!coupon) throw new NotFoundException('Coupon not found');
    if (coupon.instructorId !== instructorId) throw new ForbiddenException('Access denied');

    return this.prisma.coupon.delete({ where: { id } });
  }
}
