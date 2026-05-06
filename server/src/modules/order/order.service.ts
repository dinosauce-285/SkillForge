import { CreateOrderDto } from './dto/create.dto';
import {
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import {
  Coupon,
  CouponScope,
  NotificationType,
  OrderStatus,
  Prisma,
} from '@prisma/client';
import { NotificationsService } from '../notifications/notifications.service';
import { CouponsService } from '../coupons/coupons.service';

const PLATFORM_SHARE_RATE = 30;
const INSTRUCTOR_SHARE_RATE = 70;
const PENDING_RELEASE_DAYS = 30;

@Injectable()
export class OrderService {
  constructor(
    private prisma: PrismaService,
    private readonly notificationsService: NotificationsService,
    private readonly couponsService: CouponsService,
  ) {}

  async getAllByUser(userId: string) {
    return this.prisma.order.findMany({
      where: { userId },
      include: { course: true, transaction: true, coupon: true },
    });
  }

  async getByOrderId(userId: string, orderId: string) {
    const order = await this.prisma.order.findUnique({
      where: { id: orderId },
      include: { course: true, transaction: true, coupon: true },
    });

    if (!order) throw new NotFoundException('Order not found!');
    if (order.userId !== userId) throw new ForbiddenException('Access denied!');

    return order;
  }

  async updateOrderStatus(id: string, status: OrderStatus) {
    return this.prisma.order.update({ where: { id }, data: { status } });
  }

  async createOrder(userId: string, createOrderDto: CreateOrderDto) {
    const { courseId, couponCode } = createOrderDto;

    const course = await this.prisma.course.findUnique({ where: { id: courseId } });
    if (!course) throw new NotFoundException('Course not found');

    const originalCoursePrice = Number(course.price);
    let finalAmount = originalCoursePrice;
    let coupon: Coupon | null = null;

    if (couponCode) {
      coupon = await this.couponsService.findApplicableCouponForCheckout(
        couponCode,
        courseId,
      );
      if (coupon) {
        finalAmount = this.calculateDiscountedAmount(
          originalCoursePrice,
          coupon.discountPercent,
        );
      }
    }

    const order = await this.prisma.$transaction(async (tx) => {
      const createdOrder = await tx.order.create({
        data: {
          userId,
          courseId,
          amount: finalAmount,
          couponId: coupon?.id ?? null,
        },
        include: {
          user: true,
          course: true,
        },
      });

      await tx.enrollment.upsert({
        where: {
          userId_courseId: {
            userId,
            courseId,
          },
        },
        update: {
          status: 'ACTIVE',
        },
        create: {
          userId,
          courseId,
          status: 'ACTIVE',
        },
      });

      await tx.orderFinancialSnapshot.create({
        data: {
          orderId: createdOrder.id,
          ...this.calculateFinancialSnapshot({
            originalCoursePrice,
            customerPaidAmount: finalAmount,
            coupon,
            orderCreatedAt: createdOrder.createdAt,
          }),
        },
      });

      return createdOrder;
    });

    await this.createOrderNotifications(order);

    return order;
  }

  private calculateDiscountedAmount(
    originalCoursePrice: number,
    discountPercent: number,
  ): number {
    const discountedAmount = originalCoursePrice * (1 - discountPercent / 100);

    return this.roundCurrency(Math.max(0, discountedAmount));
  }

  private calculateFinancialSnapshot(params: {
    originalCoursePrice: number;
    customerPaidAmount: number;
    coupon: Coupon | null;
    orderCreatedAt: Date;
  }): Omit<Prisma.OrderFinancialSnapshotUncheckedCreateInput, 'orderId'> {
    const discountAmount = this.roundCurrency(
      params.originalCoursePrice - params.customerPaidAmount,
    );
    const instructorGrossRevenue = this.roundCurrency(
      params.originalCoursePrice * (INSTRUCTOR_SHARE_RATE / 100),
    );
    const platformGrossRevenue = this.roundCurrency(
      params.originalCoursePrice * (PLATFORM_SHARE_RATE / 100),
    );
    const isPlatformCoupon = params.coupon?.scope === CouponScope.PLATFORM;
    const isInstructorCoupon = params.coupon?.scope === CouponScope.INSTRUCTOR;

    return {
      originalCoursePrice: params.originalCoursePrice,
      customerPaidAmount: params.customerPaidAmount,
      couponId: params.coupon?.id ?? null,
      couponCode: params.coupon?.code ?? null,
      couponScope: params.coupon?.scope ?? null,
      discountAmount,
      discountAbsorbedByPlatform: isPlatformCoupon ? discountAmount : 0,
      discountAbsorbedByInstructor: isInstructorCoupon ? discountAmount : 0,
      platformShareRate: PLATFORM_SHARE_RATE,
      instructorShareRate: INSTRUCTOR_SHARE_RATE,
      instructorGrossRevenue,
      instructorNetRevenue: isInstructorCoupon
        ? this.roundCurrency(instructorGrossRevenue - discountAmount)
        : instructorGrossRevenue,
      platformGrossRevenue,
      platformNetRevenue: isPlatformCoupon
        ? this.roundCurrency(platformGrossRevenue - discountAmount)
        : platformGrossRevenue,
      pendingReleaseDate: this.getPendingReleaseDate(params.orderCreatedAt),
    };
  }

  private getPendingReleaseDate(orderCreatedAt: Date): Date {
    const pendingReleaseDate = new Date(orderCreatedAt);
    pendingReleaseDate.setDate(
      pendingReleaseDate.getDate() + PENDING_RELEASE_DAYS,
    );

    return pendingReleaseDate;
  }

  private roundCurrency(amount: number): number {
    return Math.round((amount + Number.EPSILON) * 100) / 100;
  }

  private async createOrderNotifications(order: {
    id: string;
    userId: string;
    courseId: string;
    course: {
      id: string;
      title: string;
      instructorId: string;
    };
  }) {
    try {
      await this.notificationsService.createNotification({
        recipientId: order.userId,
        type: NotificationType.ORDER_CREATED,
        title: 'Enrollment successful',
        message: `You are now enrolled in ${order.course.title}.`,
        metadata: {
          orderId: order.id,
          courseId: order.courseId,
          courseTitle: order.course.title,
        },
      });

      if (order.course.instructorId === order.userId) {
        return;
      }

      await this.notificationsService.createNotification({
        recipientId: order.course.instructorId,
        actorId: order.userId,
        type: NotificationType.COURSE_ENROLLMENT_CREATED,
        title: 'New course enrollment',
        message: `A student enrolled in ${order.course.title}.`,
        metadata: {
          orderId: order.id,
          courseId: order.courseId,
          courseTitle: order.course.title,
        },
      });
    } catch (error) {
      console.error('Failed to create order notifications', error);
    }
  }
}
