import { Controller, Post, UseGuards, Body, Param, Patch, Delete } from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { QuestionService } from './question.service';
import { CreateQuestionDto } from './dto/create.dto';
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

  @Patch(':id')
  @Roles(Role.INSTRUCTOR)
  update(@Param('id') id: string, @Body() updateQuestionDto: import('./dto/update.dto').UpdateQuestionDto) {
    return this.questionService.updateQuestion(id, updateQuestionDto);
  }

  @Delete(':id')
  @Roles(Role.INSTRUCTOR)
  remove(@Param('id') id: string) {
    return this.questionService.deleteQuestion(id);
  }

  @Post('reorder/:quizId')
  @Roles(Role.INSTRUCTOR)
  reorder(@Param('quizId') quizId: string, @Body() reorderDto: import('./dto/reorder.dto').ReorderQuestionsDto) {
    return this.questionService.reorderQuestions(quizId, reorderDto.orderedQuestionIds);
  }
}
