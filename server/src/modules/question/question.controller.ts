import { Controller, Post, UseGuards, Body, Get, Param, Patch, Delete } from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { QuestionService } from './question.service';
import { CreateQuestionDto } from './dto/create.dto';
import { UpdateQuestionDto } from './dto/update.dto';
import { Roles } from '../auth/decorators/roles.decorator';
import { Role } from '@prisma/client';

@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('question')
export class QuestionController {
  constructor(private readonly questionService: QuestionService) {}

  @Post(':quizId')
  @Roles(Role.INSTRUCTOR)
  create(@Param('quizId') quizId: string, @Body() createQuestionDto: CreateQuestionDto[]) {
    return this.questionService.createQuestions(quizId, createQuestionDto);
  }

  @Get('quiz/:quizId')
  @Roles(Role.INSTRUCTOR, Role.STUDENT)
  findAllByQuiz(@Param('quizId') quizId: string) {
    return this.questionService.findAllByQuiz(quizId);
  }

  @Get(':id')
  @Roles(Role.INSTRUCTOR, Role.STUDENT)
  findOne(@Param('id') id: string) {
    return this.questionService.findOne(id);
  }

  @Patch(':id')
  @Roles(Role.INSTRUCTOR)
  update(@Param('id') id: string, @Body() updateQuestionDto: UpdateQuestionDto) {
    return this.questionService.update(id, updateQuestionDto);
  }

  @Post('reorder/:quizId')
  @Roles(Role.INSTRUCTOR)
  reorder(@Param('quizId') quizId: string, @Body() body: { orderedIds: string[] }) {
    return this.questionService.reorder(quizId, body.orderedIds);
  }

  @Delete(':id')
  @Roles(Role.INSTRUCTOR)
  remove(@Param('id') id: string) {
    return this.questionService.remove(id);
  }
}
