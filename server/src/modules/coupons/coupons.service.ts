import { Injectable, NotFoundException, BadRequestException, ForbiddenException } from '@nestjs/common';
import { Coupon, CouponScope } from '@prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { CreateCouponDto } from './dto/create-coupon.dto';
import { UpdateCouponDto } from './dto/update-coupon.dto';

@Injectable()
export class CouponsService {
  constructor(private prisma: PrismaService) {}

  async createCoupon(instructorId: string, createCouponDto: CreateCouponDto) {
    const code = this.normalizeCouponCode(createCouponDto.code);
    const existing = await this.prisma.coupon.findUnique({
      where: { code },
    });

    if (existing) {
      throw new BadRequestException('Coupon code already exists');
    }

    return this.prisma.coupon.create({
      data: {
        code,
        discountPercent: createCouponDto.discountPercent,
        description: createCouponDto.description,
        maxUses: createCouponDto.maxUses,
        expiresAt: createCouponDto.expiresAt ? new Date(createCouponDto.expiresAt) : null,
        isActive: createCouponDto.isActive ?? true,
        instructorId,
        scope: createCouponDto.scope ?? CouponScope.INSTRUCTOR,
      },
    });
  }

  async getInstructorCoupons(instructorId: string) {
    return this.prisma.coupon.findMany({
      where: { instructorId, scope: CouponScope.INSTRUCTOR },
      orderBy: { createdAt: 'desc' },
    });
  }

  async validateCoupon(code: string, courseId?: string) {
    const coupon = await this.getActiveCouponByCode(code);

    await this.assertCouponAppliesToCourse(coupon, courseId);

    return {
      discountPercent: coupon.discountPercent,
      code: coupon.code,
      scope: coupon.scope,
    };
  }

  async findApplicableCouponForCheckout(
    code: string,
    courseId: string,
  ): Promise<Coupon | null> {
    const coupon = await this.prisma.coupon.findUnique({
      where: { code: this.normalizeCouponCode(code) },
    });

    if (!coupon || !coupon.isActive) {
      return null;
    }

    await this.assertCouponAppliesToCourse(coupon, courseId);

    return coupon;
  }

  async deleteCoupon(instructorId: string, id: string) {
    const coupon = await this.prisma.coupon.findUnique({ where: { id } });
    if (!coupon) throw new NotFoundException('Coupon not found');
    if (coupon.instructorId !== instructorId) throw new ForbiddenException('Access denied');

    return this.prisma.coupon.delete({ where: { id } });
  }

  async updateCoupon(instructorId: string, id: string, dto: UpdateCouponDto) {
    const coupon = await this.prisma.coupon.findUnique({ where: { id } });
    if (!coupon) throw new NotFoundException('Coupon not found');
    if (coupon.instructorId !== instructorId) throw new ForbiddenException('Access denied');

    const updateData: Record<string, unknown> = {};
    if (dto.code !== undefined) {
      const normalized = this.normalizeCouponCode(dto.code);
      const conflict = await this.prisma.coupon.findUnique({ where: { code: normalized } });
      if (conflict && conflict.id !== id) throw new BadRequestException('Coupon code already exists');
      updateData.code = normalized;
    }
    if (dto.discountPercent !== undefined) updateData.discountPercent = dto.discountPercent;
    if (dto.description !== undefined) updateData.description = dto.description;
    if (dto.maxUses !== undefined) updateData.maxUses = dto.maxUses;
    if (dto.expiresAt !== undefined) updateData.expiresAt = dto.expiresAt ? new Date(dto.expiresAt) : null;
    if (dto.isActive !== undefined) updateData.isActive = dto.isActive;

    return this.prisma.coupon.update({ where: { id }, data: updateData });
  }

  async toggleCoupon(instructorId: string, id: string) {
    const coupon = await this.prisma.coupon.findUnique({ where: { id } });
    if (!coupon) throw new NotFoundException('Coupon not found');
    if (coupon.instructorId !== instructorId) throw new ForbiddenException('Access denied');

    return this.prisma.coupon.update({
      where: { id },
      data: { isActive: !coupon.isActive },
    });
  }

  private normalizeCouponCode(code: string): string {
    const normalizedCode = code.trim().toUpperCase();
    if (!normalizedCode) {
      throw new BadRequestException('Coupon code is required');
    }

    return normalizedCode;
  }

  private async getActiveCouponByCode(code: string): Promise<Coupon> {
    const coupon = await this.prisma.coupon.findUnique({
      where: { code: this.normalizeCouponCode(code) },
    });

    if (!coupon) {
      throw new NotFoundException('Coupon not found');
    }

    if (!coupon.isActive) {
      throw new BadRequestException('Coupon is inactive');
    }

    return coupon;
  }

  private async assertCouponAppliesToCourse(
    coupon: Coupon,
    courseId?: string,
  ): Promise<void> {
    if (!courseId) {
      if (coupon.scope === CouponScope.INSTRUCTOR) {
        throw new BadRequestException('Course is required for instructor coupons');
      }

      return;
    }

    const course = await this.prisma.course.findUnique({
      where: { id: courseId },
      select: {
        instructorId: true,
        isFree: true,
        price: true,
      },
    });
    if (!course) {
      throw new NotFoundException('Course not found');
    }

    if (course.isFree || Number(course.price) <= 0) {
      throw new BadRequestException('Coupon cannot be applied to a free course');
    }

    if (coupon.scope === CouponScope.PLATFORM) {
      return;
    }

    if (course.instructorId !== coupon.instructorId) {
      throw new NotFoundException('Coupon not found');
    }
  }
}
