import { Module } from '@nestjs/common';
import { OrderController } from './order.controller';
import { OrderService } from './order.service';
import { PrismaModule } from '../prisma/prisma.module';
import { AuthModule } from '../auth/auth.module';
import { NotificationsModule } from '../notifications/notifications.module';
import { CouponsModule } from '../coupons/coupons.module';

@Module({
  imports: [PrismaModule, AuthModule, NotificationsModule, CouponsModule],
  controllers: [OrderController],
  providers: [OrderService],
})
export class OrderModule {}
