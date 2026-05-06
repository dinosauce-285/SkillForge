import {
  Controller,
  Get,
  Post,
  Patch,
  Delete,
  Body,
  Param,
  Query,
  UseGuards,
} from '@nestjs/common';
import { AdminService } from './admin.service';
import { CreateInstructorDto } from './dto/create-instructor.dto';
import { ModerateCourseDto } from './dto/moderate-course.dto';
import { CreateCouponDto } from '../coupons/dto/create-coupon.dto';
import { UpdatePlatformCouponDto } from './dto/update-platform-coupon.dto';
import {
  FinanceDateRangeQueryDto,
  FinanceSnapshotListQueryDto,
} from './dto/admin-finance-query.dto';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';

@UseGuards(JwtAuthGuard, RolesGuard)
@Roles('ADMIN')
@Controller('admin')
export class AdminController {
  constructor(private readonly adminService: AdminService) {}

  @Get('users')
  getAllUsers() {
    return this.adminService.getAllUsers();
  }

  @Patch('users/:id/ban')
  toggleUserBan(@Param('id') id: string) {
    return this.adminService.toggleUserBan(id);
  }

  @Post('users/instructor')
  createInstructor(@Body() dto: CreateInstructorDto) {
    return this.adminService.createInstructor(dto);
  }

  @Get('courses/queue')
  getCourseQueue() {
    return this.adminService.getCourseQueue();
  }

  @Get('courses/:id/preview')
  getCoursePreview(@Param('id') id: string) {
    return this.adminService.getCoursePreview(id);
  }

  @Patch('courses/:id/moderate')
  moderateCourse(
    @Param('id') id: string,
    @Body() dto: ModerateCourseDto,
  ) {
    return this.adminService.moderateCourse(id, dto);
  }

  @Post('coupons/platform')
  createPlatformCoupon(@Body() dto: CreateCouponDto) {
    return this.adminService.createPlatformCoupon(dto);
  }

  @Get('coupons/platform')
  getPlatformCoupons() {
    return this.adminService.getPlatformCoupons();
  }

  @Patch('coupons/platform/:id')
  updatePlatformCoupon(
    @Param('id') id: string,
    @Body() dto: UpdatePlatformCouponDto,
  ) {
    return this.adminService.updatePlatformCoupon(id, dto);
  }

  @Delete('coupons/platform/:id')
  deactivatePlatformCoupon(@Param('id') id: string) {
    return this.adminService.deactivatePlatformCoupon(id);
  }

  @Get('finance/summary')
  getFinanceSummary(@Query() query: FinanceDateRangeQueryDto) {
    return this.adminService.getFinanceSummary(query);
  }

  @Get('finance/snapshots')
  getFinanceSnapshots(@Query() query: FinanceSnapshotListQueryDto) {
    return this.adminService.getFinanceSnapshots(query);
  }
}
