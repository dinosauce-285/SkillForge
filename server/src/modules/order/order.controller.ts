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

@Controller('orders')
@UseGuards(JwtAuthGuard)
export class OrderController {
  constructor(private readonly orderService: OrderService) {}

  @Get()
  async getAllByUser(@Request() req) {
    const userId = req.user.id;
    return this.orderService.getAllByUser(userId);
  }

  @Get(':id')
  async getDetail(@Param('id') orderId: string, @Request() req) {
    return this.orderService.getByOrderId(req.user.id, orderId);
  }

  @Post()
  async create(@Body() createOrderDto: CreateOrderDto) {
    return this.orderService.createOrder(createOrderDto);
  }
}
