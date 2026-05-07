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

    // Check if student already submitted this quiz
    const existingAttempt = await this.prisma.quizAttempt.findFirst({
      where: {
        studentId: userId,
        quizId: quiz.id,
        status: { in: [AttemptStatus.SUBMITTED, AttemptStatus.GRADED] },
      },
    });

    if (existingAttempt) {
      throw new Error('You have already submitted this quiz.');
    }

    let correctAnswers = 0;
    const totalQuestions = quiz.questions.length;

    const answerRecords: any[] = [];

    quiz.questions.forEach((question) => {
      const answerValue = submitQuizDto.answers[question.id];
      
      if (answerValue) {
        if (quiz.isEssay) {
          // Essay answer - store text
          answerRecords.push({
            questionId: question.id,
            essayAnswer: answerValue,
          });
        } else {
          // Multiple choice answer - store selected choice ID
          const correctChoice = question.choices.find((c) => c.isCorrect);
          if (correctChoice && answerValue === correctChoice.id) {
            correctAnswers++;
          }
          
          answerRecords.push({
            questionId: question.id,
            selectedChoiceId: answerValue,
          });
        }
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
        status: quiz.isEssay ? AttemptStatus.SUBMITTED : AttemptStatus.GRADED,
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

  async getEssaySubmissions(courseId: string, studentId: string) {
    return this.prisma.quizAttempt.findMany({
      where: {
        studentId,
        quiz: {
          isEssay: true,
          chapter: {
            courseId,
          },
        },
      },
      include: {
        quiz: {
          select: {
            title: true,
          },
        },
      },
      orderBy: {
        endTime: 'desc',
      },
    });
  }

  async gradeEssayAttempt(attemptId: string, questionGrades: { questionId: string; points: number }[], feedback: string) {
    const attempt = await this.prisma.quizAttempt.findUnique({
      where: { id: attemptId },
      include: {
        quiz: {
          include: {
            questions: true,
          },
        },
      },
    });

    if (!attempt) {
      throw new Error('Attempt not found');
    }

    // Update or Create individual answers (in case student didn't answer some questions)
    for (const qGrade of questionGrades) {
      await this.prisma.studentAnswer.upsert({
        where: {
          attemptId_questionId: {
            attemptId,
            questionId: qGrade.questionId,
          },
        },
        update: {
          pointsAwarded: qGrade.points,
        },
        create: {
          attemptId,
          questionId: qGrade.questionId,
          pointsAwarded: qGrade.points,
          essayAnswer: "", // Empty answer if they didn't submit one
        },
      });
    }

    // Calculate total score based on teacher's points
    const totalScorePoints = questionGrades.reduce((acc, curr) => acc + curr.points, 0);
    const totalMaxPoints = attempt.quiz.questions.reduce((acc, curr) => acc + curr.points, 0);
    
    // Calculate percentage score
    const scorePercentage = totalMaxPoints > 0 ? (totalScorePoints / totalMaxPoints) * 100 : 0;
    const isPassed = scorePercentage >= attempt.quiz.passingScore;

    return this.prisma.quizAttempt.update({
      where: { id: attemptId },
      data: {
        score: Math.round(scorePercentage), // Store percentage as the score
        instructorFeedback: feedback,
        isPassed,
        status: 'GRADED' as any,
      },
    });
  }

  async getSubmissionDetails(attemptId: string) {
    return this.prisma.quizAttempt.findUnique({
      where: { id: attemptId },
      include: {
        quiz: {
          include: {
            questions: true,
          },
        },
        student: {
          select: {
            fullName: true,
          },
        },
        answers: {
          include: {
            question: true,
          },
        },
      },
    });
  }

  async getStudentSubmissionDetails(attemptId: string, userId: string) {
    const attempt = await this.prisma.quizAttempt.findUnique({
      where: { id: attemptId },
      include: {
        quiz: {
          include: {
            questions: {
              include: {
                choices: true,
              },
            },
          },
        },
        student: {
          select: {
            id: true,
            fullName: true,
          },
        },
        answers: {
          include: {
            question: {
              include: {
                choices: true,
              },
            },
          },
        },
      },
    });

    if (!attempt || attempt.studentId !== userId) {
      throw new Error('Submission not found or unauthorized');
    }

    return attempt;
  }
}
