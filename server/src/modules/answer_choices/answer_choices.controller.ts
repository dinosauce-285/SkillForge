import { CreateAnswerChoiceDto } from './dto/create.dto';
import { UpdateAnswerChoiceDto } from './dto/update.dto';
import { AnswerChoicesService } from './answer_choices.service';
import { Controller, UseGuards, Post, Get, Patch, Delete, Param, Body } from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { Role } from '@prisma/client';

@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('answer_choice')
export class AnswerChoiceController {
  constructor(private readonly answerChoicesService: AnswerChoicesService) {}

  @Post(':questionId')
  @Roles(Role.INSTRUCTOR)
  create(@Param('questionId') questionId: string, @Body() createAnswerChoiceDto: CreateAnswerChoiceDto[]) {
    return this.answerChoicesService.createChoices(
      questionId,
      createAnswerChoiceDto,
    );
  }

  @Get('question/:questionId')
  @Roles(Role.INSTRUCTOR, Role.STUDENT)
  findAllByQuestion(@Param('questionId') questionId: string) {
    return this.answerChoicesService.findAllByQuestion(questionId);
  }

  @Get(':id')
  @Roles(Role.INSTRUCTOR, Role.STUDENT)
  findOne(@Param('id') id: string) {
    return this.answerChoicesService.findOne(id);
  }

  @Patch(':id')
  @Roles(Role.INSTRUCTOR)
  update(@Param('id') id: string, @Body() updateAnswerChoiceDto: UpdateAnswerChoiceDto) {
    return this.answerChoicesService.update(id, updateAnswerChoiceDto);
  }

  @Delete(':id')
  @Roles(Role.INSTRUCTOR)
  remove(@Param('id') id: string) {
    return this.answerChoicesService.remove(id);
  }
}
