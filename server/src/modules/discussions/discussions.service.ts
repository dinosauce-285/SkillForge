import { Injectable, BadRequestException } from '@nestjs/common';
import { NotificationType } from '@prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { NotificationsService } from '../notifications/notifications.service';

@Injectable()
export class DiscussionsService {
  constructor(
    private prisma: PrismaService,
    private readonly notificationsService: NotificationsService,
  ) {}

  async getLessonDiscussions(lessonId: string) {
    const discussions = await this.prisma.discussion.findMany({
      where: {
        lessonId: lessonId,
        parentId: null,
      },
      include: {
        user: {
          select: {
            id: true,
            fullName: true,
            profile: {
              select: { avatarUrl: true },
            },
          },
        },
        replies: {
          include: {
            user: {
              select: {
                id: true,
                fullName: true,
                profile: {
                  select: { avatarUrl: true },
                },
              },
            },
          },
          orderBy: { createdAt: 'asc' },
        },
      },
      orderBy: { createdAt: 'desc' },
    });

    return discussions.map((discussion) => ({
      ...discussion,
      user: {
        id: discussion.user.id,
        fullName: discussion.user.fullName,
        avatarUrl: discussion.user.profile?.avatarUrl || null,
      },
      replies: discussion.replies.map((reply) => ({
        ...reply,
        user: {
          id: reply.user.id,
          fullName: reply.user.fullName,
          avatarUrl: reply.user.profile?.avatarUrl || null,
        },
        replies: [],
      })),
    }));
  }

  async createDiscussion(
    lessonId: string,
    userId: string,
    content: string,
    parentId?: string,
  ) {
    if (!content || content.trim() === '') {
      throw new BadRequestException('Content cannot be empty');
    }

    const newDiscussion = await this.prisma.discussion.create({
      data: {
        lessonId,
        userId,
        content: content.trim(),
        parentId: parentId || null,
      },
      include: {
        user: {
          select: {
            id: true,
            fullName: true,
            profile: {
              select: { avatarUrl: true },
            },
          },
        },
      },
    });

    await this.createDiscussionNotification({
      discussionId: newDiscussion.id,
      lessonId,
      userId,
      actorName: newDiscussion.user.fullName,
      parentId,
    });

    return {
      ...newDiscussion,
      user: {
        id: newDiscussion.user.id,
        fullName: newDiscussion.user.fullName,
        avatarUrl: newDiscussion.user.profile?.avatarUrl || null,
      },
      replies: [],
    };
  }

  private async createDiscussionNotification(params: {
    discussionId: string;
    lessonId: string;
    userId: string;
    actorName: string;
    parentId?: string;
  }) {
    try {
      const [lesson, parentDiscussion] = await Promise.all([
        this.prisma.lesson.findUnique({
          where: { id: params.lessonId },
          select: {
            chapter: {
              select: {
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
        params.parentId
          ? this.prisma.discussion.findUnique({
              where: { id: params.parentId },
              select: { userId: true },
            })
          : Promise.resolve(null),
      ]);

      const course = lesson?.chapter.course;
      if (!course) {
        return;
      }

      const metadata = {
        courseId: course.id,
        courseTitle: course.title,
        lessonId: params.lessonId,
        discussionId: params.discussionId,
      };

      if (params.parentId) {
        if (!parentDiscussion || parentDiscussion.userId === params.userId) {
          return;
        }

        await this.notificationsService.createNotification({
          recipientId: parentDiscussion.userId,
          actorId: params.userId,
          type: NotificationType.DISCUSSION_REPLY_CREATED,
          title: 'New discussion reply',
          message: `${params.actorName} replied to your discussion.`,
          metadata,
        });
        return;
      }

      if (course.instructorId === params.userId) {
        return;
      }

      await this.notificationsService.createNotification({
        recipientId: course.instructorId,
        actorId: params.userId,
        type: NotificationType.DISCUSSION_CREATED,
        title: 'New lesson discussion',
        message: `${params.actorName} started a discussion in ${course.title}.`,
        metadata,
      });
    } catch (error) {
      console.error('Failed to create discussion notification', error);
    }
  }
}
