import { Controller, Post, UseGuards, Body } from '@nestjs/common';
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

  @Post()
  @Roles(Role.INSTRUCTOR)
  create(quizId: string, @Body() createQuestionDto: CreateQuestionDto[]) {
    return this.questionService.createQuestions(quizId, createQuestionDto);
  }
}
