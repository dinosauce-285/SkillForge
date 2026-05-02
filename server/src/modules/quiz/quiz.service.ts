import { PrismaService } from '../prisma/prisma.service';
import { Injectable, NotFoundException } from '@nestjs/common';
import { CreateQuizDto } from './dto/create.dto';
import { UpdateQuizDto } from './dto/update.dto';
import { QuestionService } from '../question/question.service';

@Injectable()
export class QuizService {
  constructor(
    private prisma: PrismaService,
    private questionService: QuestionService,
  ) {}

  async createQuiz(quiz: CreateQuizDto) {
    return this.prisma.$transaction(async (tx) => {
      const chapter = await this.prisma.chapter.findUnique({
        where: { id: quiz.chapterId },
      });

      if (!chapter) {
        throw new NotFoundException('Chapter is not found');
      }

      const createdQuiz = await tx.quiz.create({
        data: {
          chapterId: quiz.chapterId,
          title: quiz.title,
          timeLimit: quiz.timeLimit,
          passingScore: quiz.passingScore,
          randomizeQuestions: quiz.randomizeQuestions,
        },
      });

      if (quiz.questions && quiz.questions.length > 0) {
        await this.questionService.createQuestions(
          createdQuiz.id,
          quiz.questions,
          tx,
        );
      }

      return createdQuiz;
    });
  }

  async findAllByChapter(chapterId: string) {
    return this.prisma.quiz.findMany({
      where: { chapterId },
      include: {
        questions: {
          include: {
            choices: true,
          },
          orderBy: { orderIndex: 'asc' },
        },
      },
    });
  }

  async findOne(id: string) {
    const quiz = await this.prisma.quiz.findUnique({
      where: { id },
      include: {
        questions: {
          include: {
            choices: {
              orderBy: { orderIndex: 'asc' },
            },
          },
          orderBy: { orderIndex: 'asc' },
        },
      },
    });

    if (!quiz) {
      throw new NotFoundException(`Quiz with ID ${id} not found`);
    }

    return quiz;
  }

  async update(id: string, updateQuizDto: UpdateQuizDto) {
    return this.prisma.quiz.update({
      where: { id },
      data: updateQuizDto,
    });
  }

  async remove(id: string) {
    return this.prisma.quiz.delete({
      where: { id },
    });
  }
}
