import { CreateAnswerChoiceDto } from 'src/modules/answer_choices/dto/create.dto';
import { AnswerChoicesService } from './answer_choices.service';
import { Controller, UseGuards, Post } from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { Role } from '@prisma/client';

@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('answer_choice')
export class AnswerChoiceController {
  constructor(private readonly answerChoicesService: AnswerChoicesService) {}

  @Post()
  @Roles(Role.INSTRUCTOR)
  create(questionId: string, createAnswerChoiceDto: CreateAnswerChoiceDto[]) {
    return this.answerChoicesService.createChoices(
      questionId,
      createAnswerChoiceDto,
    );
  }
}
