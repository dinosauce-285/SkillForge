import { Controller, Get, Post, Body, Param, Query, Delete, UseGuards } from '@nestjs/common';
import { CouponsService } from './coupons.service';
import { CreateCouponDto } from './dto/create-coupon.dto';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { CurrentUser } from '../auth/decorators/current-user.decorator';

@Controller('coupons')
export class CouponsController {
  constructor(private readonly couponsService: CouponsService) {}

  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles('INSTRUCTOR', 'ADMIN')
  @Post()
  create(
    @CurrentUser('id') instructorId: string,
    @Body() createCouponDto: CreateCouponDto
  ) {
    return this.couponsService.createCoupon(instructorId, createCouponDto);
  }

  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles('INSTRUCTOR', 'ADMIN')
  @Get('instructor')
  getInstructorCoupons(@CurrentUser('id') instructorId: string) {
    return this.couponsService.getInstructorCoupons(instructorId);
  }

  @Get('validate/:code')
  validateCoupon(
    @Param('code') code: string,
    @Query('courseId') courseId?: string
  ) {
    return this.couponsService.validateCoupon(code, courseId);
  }

  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles('INSTRUCTOR', 'ADMIN')
  @Delete(':id')
  deleteCoupon(
    @CurrentUser('id') instructorId: string,
    @Param('id') id: string
  ) {
    return this.couponsService.deleteCoupon(instructorId, id);
  }
}
