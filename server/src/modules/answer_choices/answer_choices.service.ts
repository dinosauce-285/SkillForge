import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateAnswerChoiceDto } from './dto/create.dto';
import { UpdateAnswerChoiceDto } from './dto/update.dto';
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

  async findAllByQuestion(questionId: string) {
    return this.prisma.answerChoice.findMany({
      where: { questionId },
      orderBy: {
        orderIndex: 'asc',
      },
    });
  }

  async findOne(id: string) {
    const choice = await this.prisma.answerChoice.findUnique({
      where: { id },
    });

    if (!choice) {
      throw new NotFoundException('Answer choice not found');
    }

    return choice;
  }

  async update(id: string, updateAnswerChoiceDto: UpdateAnswerChoiceDto) {
    const choice = await this.prisma.answerChoice.findUnique({ where: { id } });
    if (!choice) throw new NotFoundException('Answer choice not found');

    return this.prisma.answerChoice.update({
      where: { id },
      data: updateAnswerChoiceDto,
    });
  }

  async remove(id: string) {
    const choice = await this.prisma.answerChoice.findUnique({ where: { id } });
    if (!choice) throw new NotFoundException('Answer choice not found');

    return this.prisma.answerChoice.delete({
      where: { id },
    });
  }
}
