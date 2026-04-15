import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateAnswerChoiceDto } from './dto/create.dto';
import { Prisma } from '@prisma/client';

@Injectable()
export class AnswerChoicesService {
  constructor(private prisma: PrismaService) {}

  async createChoices(
    questionId: string,
    answerChoices: CreateAnswerChoiceDto[],
    tx?: Prisma.TransactionClient,
  ) {
    const client = tx || this.prisma;

    return await Promise.all(
      answerChoices.map((choice) =>
        client.answerChoice.create({
          data: {
            questionId,
            content: choice.content,
            isCorrect: choice.isCorrect,
            orderIndex: choice.orderIndex,
          },
        }),
      ),
    );
  }
}
