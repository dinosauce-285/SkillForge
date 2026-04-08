import { Injectable, BadRequestException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class DiscussionsService {
  constructor(private prisma: PrismaService) {}

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
}
