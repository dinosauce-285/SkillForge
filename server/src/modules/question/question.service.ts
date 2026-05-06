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

    await Promise.all(
      questions.map(async (question) => {
        const createdQuestion = await client.question.create({
          data: {
            quizId,
            content: question.content,
            explanation: question?.explanation,
            orderIndex: question.orderIndex,
            minWords: question.minWords,
            points: question.points,
          },
        });

        await this.answerChoicesService.createChoices(
          createdQuestion.id,
          question.choices,
          client,
        );
      }),
    );

    return client.question.findMany({
      where: { quizId },
      include: { choices: true },
    });
  }

  async updateQuestion(id: string, request: import('./dto/update.dto').UpdateQuestionDto) {
    const { choices, ...questionData } = request;

    return await this.prisma.$transaction(async (tx) => {
      const updatedQuestion = await tx.question.update({
        where: { id },
        data: questionData,
      });

      if (choices) {
        await tx.answerChoice.deleteMany({
          where: { questionId: id },
        });

        await this.answerChoicesService.createChoices(
          id,
          choices,
          tx,
        );
      }

      return tx.question.findUnique({
        where: { id },
        include: { choices: true },
      });
    });
  }

  async deleteQuestion(id: string) {
    return await this.prisma.question.delete({
      where: { id },
    });
  }

  async reorderQuestions(quizId: string, orderedQuestionIds: string[]) {
    return await this.prisma.$transaction(async (tx) => {
      const updates = orderedQuestionIds.map((id, index) =>
        tx.question.update({
            where: { id },
            data: { orderIndex: index },
        })
      );
      await Promise.all(updates);

      return tx.question.findMany({
        where: { quizId },
        orderBy: { orderIndex: 'asc' },
        include: { choices: true },
      });
    });
  }
}
