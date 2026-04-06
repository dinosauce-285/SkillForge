import {
  Body,
  Controller,
  Delete,
  Get,
  ParseUUIDPipe,
  Param,
  Patch,
  Post,
  Query,
  Request,
  UseGuards,
} from '@nestjs/common';
import { CoursesService } from './courses.service';
import { CourseListQueryDto } from './dto/course-list-query.dto';
import { CreateCourseDto } from './dto/create-course.dto';
import { UpdateCourseDto } from './dto/update-course.dto';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { CurrentUser } from '../auth/decorators/current-user.decorator';

@Controller('courses')
export class CoursesController {
  constructor(private readonly coursesService: CoursesService) {}

  @Get()
  findAll(@Query() query: CourseListQueryDto) {
    return this.coursesService.findAll(query);
  }

  @Get(':id')
  findOne(@Param('id', new ParseUUIDPipe()) id: string) {
    return this.coursesService.findOne(id);
  }

  @Get(':id/enrollment-status')
  @UseGuards(JwtAuthGuard)
  checkEnrollmentStatus(
    @Param('id', new ParseUUIDPipe()) id: string,
    @CurrentUser('userId') userId: string,
  ) {
    return this.coursesService.checkEnrollmentStatus(id, userId);
  }

  @Roles('INSTRUCTOR', 'ADMIN')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Post()
  create(@Request() req, @Body() dto: CreateCourseDto) {
    return this.coursesService.create(req.user, dto);
  }

  @Roles('INSTRUCTOR', 'ADMIN')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Patch(':id')
  update(
    @Param('id', new ParseUUIDPipe()) id: string,
    @Request() req,
    @Body() dto: UpdateCourseDto,
  ) {
    return this.coursesService.update(id, req.user, dto);
  }

  @Roles('INSTRUCTOR', 'ADMIN')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Delete(':id')
  remove(@Param('id', new ParseUUIDPipe()) id: string, @Request() req) {
    return this.coursesService.remove(id, req.user);
  }

  @UseGuards(JwtAuthGuard)
  @Roles('INSTRUCTOR', 'ADMIN')
  @Get(':id/manager')
  getCourseForManager(@Param('id') id: string, @CurrentUser() user: any) {
    return this.coursesService.getCourseForManager(id, user);
  }

  @UseGuards(JwtAuthGuard)
  @Roles('INSTRUCTOR', 'ADMIN')
  @Get('instructor/my-courses')
  findMyCourses(@CurrentUser() user: any) {
    return this.coursesService.findMyCourses(user.userId);
  }
}
