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
  UseInterceptors,
  UploadedFile,
} from '@nestjs/common';
import { CoursesService } from './courses.service';
import { CourseListQueryDto } from './dto/course-list-query.dto';
import { CreateCourseDto } from './dto/create-course.dto';
import { UpdateCourseDto } from './dto/update-course.dto';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import { FileInterceptor } from '@nestjs/platform-express';

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
    @CurrentUser('id') userId: string,
  ) {
    return this.coursesService.checkEnrollmentStatus(id, userId);
  }

  @Post()
  @Roles('INSTRUCTOR', 'ADMIN') // Ensure only instructors and admins can create courses
  @UseGuards(JwtAuthGuard, RolesGuard) // Add the authentication guards here
  @UseInterceptors(FileInterceptor('thumbnail'))
  async createCourse(
    @CurrentUser() user: any,
    @Body() dto: CreateCourseDto,
    @UploadedFile() thumbnail?: Express.Multer.File,
  ) {
    return this.coursesService.create(
      { id: user.id, role: user.role },
      dto,
      thumbnail,
    );
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
    return this.coursesService.findMyCourses(user.id);
  }
}
