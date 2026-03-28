import { CreateOrderDto } from './dto/create.dto';
import {
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { OrderStatus } from '@prisma/client';

@Injectable()
export class OrderService {
  constructor(private prisma: PrismaService) {}

  async getAllByUser(userId: string) {
    return this.prisma.order.findMany({
      where: { userId },
      include: { course: true, transaction: true },
    });
  }

  async getByOrderId(userId: string, orderId: string) {
    const order = await this.prisma.order.findUnique({
      where: { id: orderId },
      include: { course: true, transaction: true },
    });

    if (!order) throw new NotFoundException('Order not found!');
    if (order.userId !== userId) throw new ForbiddenException('Access denied!');

    return order;
  }

  async updateOrderStatus(id: string, status: OrderStatus) {
    return this.prisma.order.update({ where: { id }, data: { status } });
  }

  async createOrder(createOrderDto: CreateOrderDto) {
    const { userId, courseId, amount } = createOrderDto;

    const order = await this.prisma.order.create({
      data: {
        userId,
        courseId,
        amount,
      },
      include: {
        user: true,
        course: true,
      },
    });

    return order;
  }
}
