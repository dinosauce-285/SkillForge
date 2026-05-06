import { Injectable, NotFoundException, BadRequestException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateInstructorDto } from './dto/create-instructor.dto';
import { ModerateCourseDto } from './dto/moderate-course.dto';
import { CreateCouponDto } from '../coupons/dto/create-coupon.dto';
import { UpdatePlatformCouponDto } from './dto/update-platform-coupon.dto';
import {
  FinanceDateRangeQueryDto,
  FinanceSnapshotListQueryDto,
} from './dto/admin-finance-query.dto';
import * as bcrypt from 'bcrypt';
import { CouponScope, Prisma, Role, CourseStatus } from '@prisma/client';

const DEFAULT_FINANCE_PAGE = 1;
const DEFAULT_FINANCE_LIMIT = 20;

@Injectable()
export class AdminService {
  constructor(private readonly prisma: PrismaService) {}

  async getAllUsers() {
    return this.prisma.user.findMany({
      select: {
        id: true,
        email: true,
        fullName: true,
        role: true,
        isActive: true,
        createdAt: true,
      },
      orderBy: { createdAt: 'desc' },
    });
  }

  async toggleUserBan(id: string) {
    const user = await this.prisma.user.findUnique({ where: { id } });
    if (!user) {
      throw new NotFoundException('User not found');
    }
    if (user.role === Role.ADMIN) {
      throw new BadRequestException('Cannot ban an admin');
    }

    return this.prisma.user.update({
      where: { id },
      data: { isActive: !user.isActive },
      select: {
        id: true,
        email: true,
        isActive: true,
      },
    });
  }

  async createInstructor(dto: CreateInstructorDto) {
    const existing = await this.prisma.user.findUnique({
      where: { email: dto.email },
    });
    if (existing) {
      throw new BadRequestException('Email already in use');
    }

    const defaultPassword = 'Password123!';
    const hashedPassword = await bcrypt.hash(defaultPassword, 10);

    return this.prisma.user.create({
      data: {
        email: dto.email,
        fullName: dto.fullName,
        password: hashedPassword,
        role: Role.INSTRUCTOR,
        isActive: true,
      },
      select: {
        id: true,
        email: true,
        fullName: true,
        role: true,
      },
    });
  }

  async getCourseQueue() {
    return this.prisma.course.findMany({
      where: {
        status: CourseStatus.PENDING,
      },
      include: {
        instructor: {
          select: { id: true, fullName: true, email: true },
        },
        category: true,
      },
      orderBy: { updatedAt: 'asc' },
    });
  }

  async moderateCourse(id: string, dto: ModerateCourseDto) {
    const course = await this.prisma.course.findUnique({ where: { id } });
    if (!course) {
      throw new NotFoundException('Course not found');
    }

    const dataToUpdate: Prisma.CourseUpdateInput = { status: dto.status };
    if (dto.level) {
      dataToUpdate.level = dto.level;
    }

    return this.prisma.course.update({
      where: { id },
      data: dataToUpdate,
    });
  }

  async getCoursePreview(id: string) {
    const course = await this.prisma.course.findUnique({
      where: { id },
      include: {
        instructor: { select: { id: true, fullName: true, email: true } },
        category: true,
        chapters: {
          orderBy: { orderIndex: 'asc' },
          include: {
            lessons: {
              orderBy: { orderIndex: 'asc' },
              include: {
                materials: true,
              },
            },
            quizzes: {
              include: {
                questions: {
                  include: {
                    choices: true,
                  },
                },
              },
            },
          },
        },
      },
    });
    if (!course) {
      throw new NotFoundException('Course not found');
    }
    return course;
  }

  async createPlatformCoupon(dto: CreateCouponDto) {
    const code = this.normalizeCouponCode(dto.code);
    const existing = await this.prisma.coupon.findUnique({
      where: { code },
    });
    if (existing) {
      throw new BadRequestException('Coupon code already exists');
    }

    return this.prisma.coupon.create({
      data: {
        code,
        discountPercent: dto.discountPercent,
        isActive: dto.isActive ?? true,
        scope: CouponScope.PLATFORM,
      },
    });
  }

  async getPlatformCoupons() {
    return this.prisma.coupon.findMany({
      where: { scope: CouponScope.PLATFORM },
      orderBy: { createdAt: 'desc' },
    });
  }

  async updatePlatformCoupon(id: string, dto: UpdatePlatformCouponDto) {
    await this.getPlatformCouponOrThrow(id);

    const data: Prisma.CouponUpdateInput = {};
    if (dto.code !== undefined) {
      const code = this.normalizeCouponCode(dto.code);
      const existing = await this.prisma.coupon.findUnique({
        where: { code },
      });
      if (existing && existing.id !== id) {
        throw new BadRequestException('Coupon code already exists');
      }
      data.code = code;
    }
    if (dto.discountPercent !== undefined) {
      data.discountPercent = dto.discountPercent;
    }
    if (dto.isActive !== undefined) {
      data.isActive = dto.isActive;
    }

    return this.prisma.coupon.update({
      where: { id },
      data,
    });
  }

  async deactivatePlatformCoupon(id: string) {
    await this.getPlatformCouponOrThrow(id);

    return this.prisma.coupon.update({
      where: { id },
      data: { isActive: false },
    });
  }

  async getFinanceSummary(query: FinanceDateRangeQueryDto) {
    const baseWhere = this.buildFinanceDateWhere(query);
    const now = new Date();

    const [total, pendingInstructor, availableInstructor] = await Promise.all([
      this.prisma.orderFinancialSnapshot.aggregate({
        where: baseWhere,
        _sum: {
          customerPaidAmount: true,
          platformNetRevenue: true,
        },
      }),
      this.prisma.orderFinancialSnapshot.aggregate({
        where: {
          ...baseWhere,
          pendingReleaseDate: { gt: now },
        },
        _sum: {
          instructorNetRevenue: true,
        },
      }),
      this.prisma.orderFinancialSnapshot.aggregate({
        where: {
          ...baseWhere,
          pendingReleaseDate: { lte: now },
        },
        _sum: {
          instructorNetRevenue: true,
        },
      }),
    ]);

    return {
      grossRevenue: this.toNumber(total._sum.customerPaidAmount),
      netPlatformRevenue: this.toNumber(total._sum.platformNetRevenue),
      pendingInstructorBalance: this.toNumber(
        pendingInstructor._sum.instructorNetRevenue,
      ),
      availableInstructorBalance: this.toNumber(
        availableInstructor._sum.instructorNetRevenue,
      ),
    };
  }

  async getFinanceSnapshots(query: FinanceSnapshotListQueryDto) {
    const page = query.page ?? DEFAULT_FINANCE_PAGE;
    const limit = query.limit ?? DEFAULT_FINANCE_LIMIT;
    const where = this.buildFinanceDateWhere(query);

    const [total, snapshots] = await this.prisma.$transaction([
      this.prisma.orderFinancialSnapshot.count({ where }),
      this.prisma.orderFinancialSnapshot.findMany({
        where,
        orderBy: { createdAt: 'desc' },
        skip: (page - 1) * limit,
        take: limit,
        include: {
          order: {
            select: {
              id: true,
              status: true,
              createdAt: true,
              course: {
                select: {
                  id: true,
                  title: true,
                  instructorId: true,
                },
              },
            },
          },
        },
      }),
    ]);

    return {
      data: snapshots.map((snapshot) => ({
        id: snapshot.id,
        orderId: snapshot.orderId,
        orderStatus: snapshot.order.status,
        orderCreatedAt: snapshot.order.createdAt,
        courseId: snapshot.order.course.id,
        courseTitle: snapshot.order.course.title,
        instructorId: snapshot.order.course.instructorId,
        originalCoursePrice: this.toNumber(snapshot.originalCoursePrice),
        customerPaidAmount: this.toNumber(snapshot.customerPaidAmount),
        couponId: snapshot.couponId,
        couponCode: snapshot.couponCode,
        couponScope: snapshot.couponScope,
        discountAmount: this.toNumber(snapshot.discountAmount),
        discountAbsorbedByPlatform: this.toNumber(
          snapshot.discountAbsorbedByPlatform,
        ),
        discountAbsorbedByInstructor: this.toNumber(
          snapshot.discountAbsorbedByInstructor,
        ),
        platformShareRate: snapshot.platformShareRate,
        instructorShareRate: snapshot.instructorShareRate,
        instructorGrossRevenue: this.toNumber(snapshot.instructorGrossRevenue),
        instructorNetRevenue: this.toNumber(snapshot.instructorNetRevenue),
        platformGrossRevenue: this.toNumber(snapshot.platformGrossRevenue),
        platformNetRevenue: this.toNumber(snapshot.platformNetRevenue),
        pendingReleaseDate: snapshot.pendingReleaseDate,
        createdAt: snapshot.createdAt,
      })),
      meta: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit),
      },
    };
  }

  private normalizeCouponCode(code: string): string {
    const normalizedCode = code.trim().toUpperCase();
    if (!normalizedCode) {
      throw new BadRequestException('Coupon code is required');
    }

    return normalizedCode;
  }

  private async getPlatformCouponOrThrow(id: string) {
    const coupon = await this.prisma.coupon.findUnique({ where: { id } });
    if (!coupon || coupon.scope !== CouponScope.PLATFORM) {
      throw new NotFoundException('Platform coupon not found');
    }

    return coupon;
  }

  private buildFinanceDateWhere(
    query: FinanceDateRangeQueryDto,
  ): Prisma.OrderFinancialSnapshotWhereInput {
    const createdAt: Prisma.DateTimeFilter = {};
    if (query.startDate) {
      createdAt.gte = new Date(query.startDate);
    }
    if (query.endDate) {
      createdAt.lte = new Date(query.endDate);
    }

    if (Object.keys(createdAt).length === 0) {
      return {};
    }

    return { createdAt };
  }

  private toNumber(value: Prisma.Decimal | null | undefined): number {
    return Number(value ?? 0);
  }
}
