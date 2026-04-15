import { Module } from '@nestjs/common';
import { PrismaModule } from '../prisma/prisma.module';
import { QuestionController } from './question.controller';
import { QuestionService } from './question.service';
import { AnswerChoicesModule } from '../answer_choices/answer_choices.module';

@Module({
  imports: [PrismaModule, AnswerChoicesModule],
  controllers: [QuestionController],
  providers: [QuestionService],
  exports: [QuestionService],
})
export class QuestionModule {}
