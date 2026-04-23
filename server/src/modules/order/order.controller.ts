import {
  Controller,
  Get,
  Param,
  Post,
  Body,
  UseGuards,
  Request,
} from '@nestjs/common';
import { OrderService } from './order.service';
import { CreateOrderDto } from './dto/create.dto';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';

@Controller('orders')
@UseGuards(JwtAuthGuard)
export class OrderController {
  constructor(private readonly orderService: OrderService) {}

  @Get()
  async getAllByUser(@CurrentUser('id') userId: string) {
    return this.orderService.getAllByUser(userId);
  }

  @Get(':id')
  async getDetail(
    @Param('id') orderId: string,
    @CurrentUser('id') userId: string,
  ) {
    return this.orderService.getByOrderId(userId, orderId);
  }

  @Post()
  async create(
    @Body() createOrderDto: CreateOrderDto,
    @CurrentUser('id') userId: string,
  ) {
    return this.orderService.createOrder(userId, createOrderDto);
  }
}
