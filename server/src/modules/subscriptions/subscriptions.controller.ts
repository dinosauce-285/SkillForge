import { Body, Controller, Post, UseGuards } from '@nestjs/common';
import { CreateInstructorSubscriptionDto } from './dto/create-instructor-subscription.dto';
import { SubscriptionsService } from './subscriptions.service';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';

@Controller('subscriptions')
@UseGuards(JwtAuthGuard)
export class SubscriptionsController {
  constructor(private readonly subscriptionsService: SubscriptionsService) {}

  @Post('instructor')
  createInstructorSubscription(
    @CurrentUser('id') userId: string,
    @Body() dto: CreateInstructorSubscriptionDto,
  ) {
    return this.subscriptionsService.createInstructorSubscription(userId, dto);
  }
}
