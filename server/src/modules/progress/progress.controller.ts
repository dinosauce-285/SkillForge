import { Body, Controller, Get, Param, ParseUUIDPipe, Post, UseGuards } from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { ProgressService } from './progress.service';
import { CurrentUser } from '../auth/decorators/current-user.decorator';

@Controller('progress')
@UseGuards(JwtAuthGuard) // Bắt buộc đăng nhập cho toàn bộ module này
export class ProgressController {
    constructor(private readonly progressService: ProgressService) { }

    @Post('lessons/:lessonId/mark')
    markLeseson(
        @CurrentUser('userId') userId: string,
        @Param('lessonId', new ParseUUIDPipe()) lessonId: string,
        @Body('isCompleted') isCompleted?: boolean,
    ) {

        const status = isCompleted !== undefined ? isCompleted : true;
        return this.progressService.markLessonCompleted(userId, lessonId, status);
    }

    @Get('dashboard')
    getDashboard(@CurrentUser('userId') userId: string) {
        return this.progressService.getDashboardProgress(userId)
    }

    @Get('courses/:courseId')
    getCourseProgress(
        @CurrentUser('userId') userId: string,
        @Param('courseId', new ParseUUIDPipe()) courseId: string,
    ) {
        return this.progressService.getCourseProgress(userId, courseId);
    }
}
