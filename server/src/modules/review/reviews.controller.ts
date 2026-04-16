import { Controller, Post, Get, Body, Param, UseGuards } from '@nestjs/common';
import { ReviewsService } from './reviews.service';
import { CreateReviewDto } from './dto/create-review.dto';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';

@Controller('courses/:courseId/reviews')
export class ReviewsController {
  constructor(private readonly reviewsService: ReviewsService) {}

  @UseGuards(JwtAuthGuard)
  @Post()
  createReview(
    @CurrentUser('userId') userId: string,
    @Param('courseId') courseId: string,
    @Body() dto: CreateReviewDto
  ) {
    return this.reviewsService.createReview(userId, courseId, dto);
  }

  @Get()
  getReviews(@Param('courseId') courseId: string) {
    return this.reviewsService.getCourseReviews(courseId);
  }
}