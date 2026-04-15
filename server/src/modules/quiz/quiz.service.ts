import { PrismaService } from '../prisma/prisma.service';
import { Injectable } from '@nestjs/common';
import { CreateQuizDto } from './dto/create.dto';
import { QuestionService } from '../question/question.service';
import { NotFoundException } from '@nestjs/common';

@Injectable()
export class QuizService {
  constructor(
    private prisma: PrismaService,
    private questionService: QuestionService,
  ) {}

  async createQuiz(quiz: CreateQuizDto) {
    return this.prisma.$transaction(async (tx) => {
      const lesson = await this.prisma.lesson.findUnique({
        where: { id: quiz.lessonId },
      });

      if (!lesson) {
        throw new NotFoundException('Lesson is not found');
      }

      const createdQuiz = await tx.quiz.create({
        data: {
          lessonId: quiz.lessonId,
          timeLimit: quiz.timeLimit,
          passingScore: quiz.passingScore,
          randomizeQuestions: quiz.randomizeQuestions,
        },
      });

      await this.questionService.createQuestions(
        createdQuiz.id,
        quiz.questions,
        tx,
      );

      return createdQuiz;
    });
  }
}
