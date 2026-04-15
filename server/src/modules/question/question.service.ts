import { CreateQuestionDto } from './dto/create.dto';
import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { Prisma } from '@prisma/client';
import { AnswerChoicesService } from '../answer_choices/answer_choices.service';

@Injectable()
export class QuestionService {
  constructor(
    private prisma: PrismaService,
    private answerChoicesService: AnswerChoicesService,
  ) {}

  async createQuestions(
    quizId: string,
    questions: CreateQuestionDto[],
    tx?: Prisma.TransactionClient,
  ) {
    const client = tx || this.prisma;

    return await Promise.all(
      questions.map(async (question) => {
        const createdQuestion = await client.question.create({
          data: {
            quizId,
            content: question.content,
            explanation: question?.explanation,
            orderIndex: question.orderIndex,
          },
        });

        await this.answerChoicesService.createChoices(
          createdQuestion.id,
          question.choices,
          client,
        );
      }),
    );
  }
}
