import { Injectable, NotFoundException } from '@nestjs/common';
import { CreateQuizDto } from './dto/create.dto';
import { UpdateQuizDto } from './dto/update.dto';
import { SubmitQuizDto } from './dto/submit.dto';
import { QuestionService } from '../question/question.service';
import { PrismaService } from '../prisma/prisma.service';
import { AttemptStatus } from '@prisma/client';

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
          isEssay: quiz.isEssay,
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

  async submitQuiz(userId: string, quizId: string, submitQuizDto: SubmitQuizDto) {
    const quiz = await this.prisma.quiz.findUnique({
      where: { id: quizId },
      include: {
        questions: {
          include: {
            choices: true,
          },
        },
      },
    });

    if (!quiz) {
      throw new NotFoundException(`Quiz with ID ${quizId} not found`);
    }

    let correctAnswers = 0;
    const totalQuestions = quiz.questions.length;

    const answerRecords: any[] = [];

    quiz.questions.forEach((question) => {
      const selectedChoiceId = submitQuizDto.answers[question.id];
      const correctChoice = question.choices.find((c) => c.isCorrect);

      if (selectedChoiceId && correctChoice && selectedChoiceId === correctChoice.id) {
        correctAnswers++;
      }

      if (selectedChoiceId) {
        answerRecords.push({
          questionId: question.id,
          selectedChoiceId: selectedChoiceId,
        });
      }
    });

    const score = totalQuestions > 0 ? Math.round((correctAnswers / totalQuestions) * 100) : 0;
    const isPassed = score >= quiz.passingScore;

    const attempt = await this.prisma.quizAttempt.create({
      data: {
        studentId: userId,
        quizId: quiz.id,
        score: score,
        isPassed: isPassed,
        status: AttemptStatus.SUBMITTED,
        endTime: new Date(),
        answers: {
          create: answerRecords,
        },
      },
    });

    return {
      attemptId: attempt.id,
      score: score,
      isPassed: isPassed,
      correctAnswers: correctAnswers,
      totalQuestions: totalQuestions,
    };
  }
}
