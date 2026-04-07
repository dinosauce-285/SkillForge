import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
  UseGuards,
  UseInterceptors,
  UploadedFile,
  BadRequestException,
} from '@nestjs/common';
import { Role } from '@prisma/client';
import { Roles } from '../auth/decorators/roles.decorator';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { LessonsService } from './lessons.service';
import { CreateLessonDto } from './dto/create-lesson.dto';
import { UpdateLessonDto } from './dto/update-lesson.dto';
import { FileInterceptor } from '@nestjs/platform-express';
import { memoryStorage } from 'multer';

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

  @Post(':lessonId/materials')
  @UseInterceptors(
    FileInterceptor('file', {
      storage: memoryStorage(), // 🌟 Giữ file ở trên RAM (Buffer)
      limits: {
        fileSize: 1024 * 1024 * 100, // Tối đa 100MB
      },
    }),
  )
  async uploadMaterial(
    @Param('lessonId') lessonId: string,
    @Body('title') title: string,
    @Body('type') type: string,
    @UploadedFile() file: Express.Multer.File,
  ) {
    if (!file) throw new BadRequestException('Chưa đính kèm file!');

    // Giao file (dạng Buffer) cho Service bắn lên Cloud
    return this.lessonsService.addMaterialToLesson(lessonId, title, type, file);
  }
}
