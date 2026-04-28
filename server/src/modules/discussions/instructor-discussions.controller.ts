import { Controller, Get, Post, Body, Param, Query, UseGuards } from '@nestjs/common';
import { DiscussionsService } from './discussions.service';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import { CreateDiscussionDto } from './dto/create-discussion.dto';
import { Role } from '@prisma/client';
import { Roles } from '../auth/decorators/roles.decorator';
import { RolesGuard } from '../auth/guards/roles.guard';

@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('instructor/discussions')
export class InstructorDiscussionsController {
  constructor(private readonly discussionsService: DiscussionsService) {}

  @Get()
  @Roles(Role.INSTRUCTOR, Role.ADMIN)
  getInstructorDiscussions(
    @CurrentUser() user: any,
    @Query('courseId') courseId?: string,
    @Query('unansweredOnly') unansweredOnly?: string,
  ) {
    const isUnansweredOnly = unansweredOnly === 'true';
    return this.discussionsService.getInstructorDiscussions(user.id, courseId, isUnansweredOnly);
  }

  @Post(':id/reply')
  @Roles(Role.INSTRUCTOR, Role.ADMIN)
  replyToDiscussion(
    @Param('id') discussionId: string,
    @CurrentUser() user: any,
    @Body() dto: { content: string; lessonId: string },
  ) {
    return this.discussionsService.createDiscussion(
      dto.lessonId,
      user.id,
      dto.content,
      discussionId, // discussionId becomes the parentId
    );
  }
}
