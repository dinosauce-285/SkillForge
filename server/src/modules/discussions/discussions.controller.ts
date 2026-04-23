import { Controller, Get, Post, Body, Param, UseGuards } from '@nestjs/common';
import { DiscussionsService } from './discussions.service';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import { CreateDiscussionDto } from './dto/create-discussion.dto';
import { Role } from '@prisma/client';
import { Roles } from '../auth/decorators/roles.decorator';
import { RolesGuard } from '../auth/guards/roles.guard';

@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('lessons/:lessonId/discussions')
export class DiscussionsController {
  constructor(private readonly discussionsService: DiscussionsService) {}

  @Get()
  @Roles(Role.STUDENT, Role.INSTRUCTOR, Role.ADMIN)
  getDiscussions(@Param('lessonId') lessonId: string) {
    return this.discussionsService.getLessonDiscussions(lessonId);
  }

  @Post()
  @Roles(Role.STUDENT, Role.INSTRUCTOR, Role.ADMIN)
  addDiscussion(
    @Param('lessonId') lessonId: string,
    @CurrentUser() user: any,
    @Body() createDiscussionDto: CreateDiscussionDto,
  ) {
    return this.discussionsService.createDiscussion(
      lessonId,
      user.id,
      createDiscussionDto.content,
      createDiscussionDto.parentId,
    );
  }
}
