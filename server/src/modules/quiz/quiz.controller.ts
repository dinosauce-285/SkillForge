import { Controller, UseGuards, Post, Body, Get, Param, Patch, Delete } from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { Role } from '@prisma/client';
import { QuizService } from './quiz.service';
import { CreateQuizDto } from './dto/create.dto';
import { UpdateQuizDto } from './dto/update.dto';
import { SubmitQuizDto } from './dto/submit.dto';
import { CurrentUser } from '../auth/decorators/current-user.decorator';

@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('quiz')
export class QuizController {
  constructor(private readonly quizService: QuizService) {}

  @Post()
  @Roles(Role.INSTRUCTOR)
  create(@Body() createQuizDto: CreateQuizDto) {
    return this.quizService.createQuiz(createQuizDto);
  }

  @Get('chapter/:chapterId')
  @Roles(Role.INSTRUCTOR, Role.STUDENT)
  findAllByChapter(@Param('chapterId') chapterId: string) {
    return this.quizService.findAllByChapter(chapterId);
  }

  @Get(':id')
  @Roles(Role.INSTRUCTOR, Role.STUDENT)
  findOne(@Param('id') id: string) {
    return this.quizService.findOne(id);
  }

  @Patch(':id')
  @Roles(Role.INSTRUCTOR)
  update(@Param('id') id: string, @Body() updateQuizDto: UpdateQuizDto) {
    return this.quizService.update(id, updateQuizDto);
  }

  @Delete(':id')
  @Roles(Role.INSTRUCTOR)
  remove(@Param('id') id: string) {
    return this.quizService.remove(id);
  }

  @Post(':id/submit')
  @Roles(Role.STUDENT)
  submit(
    @Param('id') id: string,
    @Body() submitQuizDto: SubmitQuizDto,
    @CurrentUser('id') userId: string
  ) {
    return this.quizService.submitQuiz(userId, id, submitQuizDto);
  }
}
