import { Module } from '@nestjs/common';
import { AnswerChoiceController } from './answer_choices.controller';
import { AnswerChoicesService } from './answer_choices.service';
import { PrismaModule } from '../prisma/prisma.module';

@Module({
  imports: [PrismaModule],
  controllers: [AnswerChoiceController],
  providers: [AnswerChoicesService],
  exports: [AnswerChoicesService],
})
export class AnswerChoicesModule {}
