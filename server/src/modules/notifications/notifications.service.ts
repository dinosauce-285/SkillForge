import { Injectable, NotFoundException } from '@nestjs/common';
import { NotificationType, Prisma } from '@prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { NotificationListQueryDto } from './dto/notification-list-query.dto';

export interface CreateNotificationPayload {
  readonly recipientId: string;
  readonly actorId?: string;
  readonly type: NotificationType;
  readonly title: string;
  readonly message: string;
  readonly metadata?: Prisma.InputJsonValue;
}

@Injectable()
export class NotificationsService {
  constructor(private readonly prisma: PrismaService) {}

  async createNotification(payload: CreateNotificationPayload) {
    return this.prisma.notification.create({
      data: {
        recipientId: payload.recipientId,
        actorId: payload.actorId,
        type: payload.type,
        title: payload.title,
        message: payload.message,
        ...(payload.metadata !== undefined
          ? { metadata: payload.metadata }
          : {}),
      },
    });
  }

  async getNotificationsByUser(
    userId: string,
    query: NotificationListQueryDto,
  ) {
    const page = query.page ?? 1;
    const limit = query.limit ?? 20;
    const where: Prisma.NotificationWhereInput = {
      recipientId: userId,
      ...(query.unreadOnly ? { readAt: null } : {}),
    };

    const [total, unreadCount, notifications] = await this.prisma.$transaction([
      this.prisma.notification.count({ where }),
      this.getUnreadCountQuery(userId),
      this.prisma.notification.findMany({
        where,
        skip: (page - 1) * limit,
        take: limit,
        orderBy: { createdAt: 'desc' },
        include: {
          actor: {
            select: {
              id: true,
              fullName: true,
              profile: {
                select: {
                  avatarUrl: true,
                },
              },
            },
          },
        },
      }),
    ]);

    return {
      data: notifications,
      meta: {
        total,
        page,
        limit,
        totalPages: Math.ceil(total / limit),
        unreadCount,
      },
    };
  }

  async markAsRead(notificationId: string, userId: string) {
    const notification = await this.prisma.notification.findFirst({
      where: {
        id: notificationId,
        recipientId: userId,
      },
      select: {
        id: true,
        readAt: true,
      },
    });

    if (!notification) {
      throw new NotFoundException('Notification not found');
    }

    if (notification.readAt) {
      return notification;
    }

    return this.prisma.notification.update({
      where: { id: notificationId },
      data: { readAt: new Date() },
      select: {
        id: true,
        readAt: true,
      },
    });
  }

  async markAllAsRead(userId: string) {
    const result = await this.prisma.notification.updateMany({
      where: {
        recipientId: userId,
        readAt: null,
      },
      data: {
        readAt: new Date(),
      },
    });

    return {
      updatedCount: result.count,
    };
  }

  async getUnreadCount(userId: string) {
    return this.getUnreadCountQuery(userId);
  }

  private getUnreadCountQuery(userId: string) {
    return this.prisma.notification.count({
      where: {
        recipientId: userId,
        readAt: null,
      },
    });
  }
}
