import { CreateOrderDto } from './dto/create.dto';
import {
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { NotificationType, OrderStatus } from '@prisma/client';
import { NotificationsService } from '../notifications/notifications.service';

@Injectable()
export class OrderService {
  constructor(
    private prisma: PrismaService,
    private readonly notificationsService: NotificationsService,
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

    let finalAmount = Number(course.price);
    let couponId: string | null = null;

    if (couponCode) {
      const coupon = await this.prisma.coupon.findUnique({ where: { code: couponCode.toUpperCase() } });
      if (coupon && coupon.isActive) {
        finalAmount = finalAmount * (1 - (coupon.discountPercent / 100));
        couponId = coupon.id;
      }
    }

    const [order] = await this.prisma.$transaction([
      this.prisma.order.create({
        data: {
          userId,
          courseId,
          amount: finalAmount,
          couponId,
        },
        include: {
          user: true,
          course: true,
        },
      }),
      this.prisma.enrollment.upsert({
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
      }),
    ]);

    await this.createOrderNotifications(order);

    return order;
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
