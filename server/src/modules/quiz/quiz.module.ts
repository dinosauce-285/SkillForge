import { Module } from '@nestjs/common';
import { PrismaModule } from '../prisma/prisma.module';
import { QuizService } from './quiz.service';
import { QuizController } from './quiz.controller';
import { QuestionModule } from '../question/question.module';

@Module({
  imports: [PrismaModule, QuestionModule],
  controllers: [QuizController],
  providers: [QuizService],
  exports: [QuizService],
})
export class QuizModule {}
