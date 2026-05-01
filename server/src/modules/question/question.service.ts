import { CreateQuestionDto } from './dto/create.dto';
import { UpdateQuestionDto } from './dto/update.dto';
import { Injectable, NotFoundException } from '@nestjs/common';
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

  async findAllByQuiz(quizId: string) {
    return this.prisma.question.findMany({
      where: { quizId },
      include: {
        choices: true,
      },
      orderBy: {
        orderIndex: 'asc',
      },
    });
  }

  async findOne(id: string) {
    const question = await this.prisma.question.findUnique({
      where: { id },
      include: {
        choices: true,
      },
    });

    if (!question) {
      throw new NotFoundException('Question not found');
    }

    return question;
  }

  async update(id: string, updateQuestionDto: UpdateQuestionDto) {
    const question = await this.prisma.question.findUnique({ where: { id } });
    if (!question) throw new NotFoundException('Question not found');

    return this.prisma.$transaction(async (tx) => {
      const updatedQuestion = await tx.question.update({
        where: { id },
        data: {
          content: updateQuestionDto.content,
          explanation: updateQuestionDto.explanation,
          orderIndex: updateQuestionDto.orderIndex,
        },
      });

      if (updateQuestionDto.choices) {
        await tx.answerChoice.deleteMany({
          where: { questionId: id },
        });

        await this.answerChoicesService.createChoices(
          id,
          updateQuestionDto.choices,
          tx,
        );
      }

      return tx.question.findUnique({
        where: { id },
        include: { choices: true },
      });
    });
  }

  async reorder(quizId: string, orderedIds: string[]) {
    return this.prisma.$transaction(async (tx) => {
      await Promise.all(
        orderedIds.map((questionId, index) =>
          tx.question.update({
            where: { id: questionId },
            data: { orderIndex: index },
          }),
        ),
      );

      return tx.question.findMany({
        where: { quizId },
        include: { choices: true },
        orderBy: { orderIndex: 'asc' },
      });
    });
  }

  async remove(id: string) {
    const question = await this.prisma.question.findUnique({ where: { id } });
    if (!question) throw new NotFoundException('Question not found');

    return this.prisma.question.delete({
      where: { id },
    });
  }
}
