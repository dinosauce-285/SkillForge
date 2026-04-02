import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
  UseGuards,
} from '@nestjs/common';
import { Role } from '@prisma/client';
import { Roles } from '../auth/decorators/roles.decorator';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { LessonsService } from './lessons.service';
import { CreateLessonDto } from './dto/create-lesson.dto';
import { UpdateLessonDto } from './dto/update-lesson.dto';

@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('lessons')
export class LessonsController {
  constructor(private readonly lessonsService: LessonsService) {}

  // API returns lesson content. Students, Instructors, and Admins can view.
  @Get(':id')
  @Roles(Role.STUDENT, Role.INSTRUCTOR, Role.ADMIN)
  findOne(@Param('id') id: string, @CurrentUser() user: any) {
    return this.lessonsService.findOne(id, user);
  }

  // Only INSTRUCTOR can CRUD lessons
  @Post()
  @Roles(Role.INSTRUCTOR)
  create(@CurrentUser() user: any, @Body() createLessonDto: CreateLessonDto) {
    return this.lessonsService.create(user, createLessonDto);
  }

  @Patch(':id')
  @Roles(Role.INSTRUCTOR)
  update(
    @Param('id') id: string,
    @CurrentUser() user: any,
    @Body() updateLessonDto: UpdateLessonDto,
  ) {
    return this.lessonsService.update(id, user, updateLessonDto);
  }

  @Delete(':id')
  @Roles(Role.INSTRUCTOR)
  remove(@Param('id') id: string, @CurrentUser() user: any) {
    return this.lessonsService.remove(id, user);
  }
}
