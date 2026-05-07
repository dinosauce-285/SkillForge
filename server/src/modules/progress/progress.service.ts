import { Injectable, NotFoundException, BadRequestException } from '@nestjs/common';
import { NotificationType } from '@prisma/client';
import PDFDocument from 'pdfkit';
import { NotificationsService } from '../notifications/notifications.service';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class ProgressService {
  constructor(
    private readonly prisma: PrismaService,
    private readonly notificationsService: NotificationsService,
  ) {}

  async markLessonCompleted(
    userId: string,
    lessonId: string,
    isCompleted: boolean = true,
  ) {
    const lesson = await this.prisma.lesson.findFirst({
      where: {
        id: lessonId,
        deletedAt: null,
      },
      select: {
        id: true,
        chapter: {
          select: {
            course: {
              select: {
                id: true,
                title: true,
              },
            },
          },
        },
      },
    });

    if (!lesson) {
      throw new NotFoundException(`Lesson with id "${lessonId}" not found.`);
    }

    const lessonProgress = await this.prisma.lessonProgress.upsert({
      where: {
        userId_lessonId: { userId, lessonId },
      },
      update: { isCompleted },
      create: { userId, lessonId, isCompleted },
    });

    if (isCompleted) {
      await this.createCourseCompletedNotification({
        userId,
        lessonId,
        courseId: lesson.chapter.course.id,
        courseTitle: lesson.chapter.course.title,
      });
    }

    return lessonProgress;
  }

  // Get % of the course (Use in Course Details)
  async getCourseProgress(userId: string, courseId: string) {
    // Get the number of lesson of this course
    const course = await this.prisma.course.findUnique({
      where: { id: courseId },
      include: {
        chapters: {
          where: { deletedAt: null },
          include: {
            _count: {
              select: {
                lessons: {
                  where: { deletedAt: null },
                },
                quizzes: true,
              },
            },
          },
        },
      },
    });

    if (!course) {
      throw new NotFoundException(`Course with id "${courseId}" not found`);
    }

    // Sum lesson and quizzes
    const totalLessons = course.chapters.reduce(
      (sum, chap) => sum + chap._count.lessons,
      0,
    );
    const totalQuizzes = course.chapters.reduce(
      (sum, chap) => sum + chap._count.quizzes,
      0,
    );
    const totalItems = totalLessons + totalQuizzes;

    // Get completed lessons records
    const completedLessonsData = await this.prisma.lessonProgress.findMany({
      where: {
        userId,
        isCompleted: true,
        lesson: {
          chapter: {
            courseId: courseId,
          },
        },
      },
      select: {
        lessonId: true,
      },
    });

    const completedLessonIds = completedLessonsData.map((data) => data.lessonId);
    const completedLessonsCount = completedLessonIds.length;

    // Get completed quizzes records (MCQ submitted or Essay graded)
    const completedQuizzesData = await this.prisma.quizAttempt.findMany({
      where: {
        studentId: userId,
        status: { in: ['SUBMITTED', 'GRADED'] },
        quiz: {
          chapter: {
            courseId: courseId,
          },
        },
      },
      orderBy: {
        startTime: 'desc',
      },
      distinct: ['quizId'],
      select: {
        id: true,
        quizId: true,
        isPassed: true,
        status: true,
      },
    });

    const quizStatuses = completedQuizzesData.map((data) => ({
      quizId: data.quizId,
      attemptId: data.id,
      isPassed: data.isPassed,
      status: data.status,
    }));

    const completedQuizIds = completedQuizzesData.map((data) => data.quizId);
    const completedQuizzesCount = completedQuizIds.length;

    const completedItemsCount = completedLessonsCount + completedQuizzesCount;

    // Calculate percent
    const percentage =
      totalItems === 0
        ? 0
        : Math.round((completedItemsCount / totalItems) * 100);

    return {
      courseId,
      totalLessons,
      completedLessons: completedLessonsCount,
      percentage,
      completedLessonIds,
      completedQuizIds,
      quizStatuses,
    };
  }

  async getDashboardData(userId: string) {
    const [user, enrollments] = await Promise.all([
      this.prisma.user.findUnique({
        where: { id: userId },
        select: { fullName: true },
      }),
      this.prisma.enrollment.findMany({
        where: { userId: userId, status: 'ACTIVE' },
        orderBy: { enrolledAt: 'desc' },
        include: {
          course: {
            select: {
              id: true,
              title: true,
              thumbnailUrl: true,
              instructor: { select: { fullName: true } },
            },
          },
        },
      }),
    ]);

    if (!user) {
      throw new NotFoundException(`User with id "${userId}" not found.`);
    }

    if (enrollments.length === 0) {
      return {
        studentName: user.fullName,
        hoursSpent: 0,
        badgesEarned: 0,
        courses: [],
      };
    }

    const courseIds = enrollments.map((e) => e.courseId);

    const coursesWithLessons = await this.prisma.course.findMany({
      where: { id: { in: courseIds } },
      select: {
        id: true,
        chapters: {
          where: { deletedAt: null },
          select: {
            _count: { select: { lessons: { where: { deletedAt: null } }, quizzes: true } },
          },
        },
      },
    });

    const totalLessonsMap = new Map<string, number>();
    coursesWithLessons.forEach((course) => {
      const total = course.chapters.reduce(
        (sum, chap) => sum + chap._count.lessons + chap._count.quizzes,
        0,
      );
      totalLessonsMap.set(course.id, total);
    });

    const completedLessonsData = await this.prisma.lessonProgress.findMany({
      where: {
        userId,
        isCompleted: true,
        lesson: { chapter: { courseId: { in: courseIds } } },
      },
      select: {
        lesson: { select: { chapter: { select: { courseId: true } } } },
      },
    });

    const completedQuizzesData = await this.prisma.quizAttempt.findMany({
      where: {
        studentId: userId,
        status: { in: ['SUBMITTED', 'GRADED'] },
        quiz: { chapter: { courseId: { in: courseIds } } },
      },
      distinct: ['quizId'],
      select: {
        quiz: { select: { chapter: { select: { courseId: true } } } },
      },
    });

    const completedItemsMap = new Map<string, number>();
    completedLessonsData.forEach((progress) => {
      const cId = progress.lesson.chapter.courseId;
      completedItemsMap.set(cId, (completedItemsMap.get(cId) || 0) + 1);
    });
    completedQuizzesData.forEach((attempt) => {
      const cId = attempt.quiz.chapter.courseId;
      completedItemsMap.set(cId, (completedItemsMap.get(cId) || 0) + 1);
    });

    let totalCompletedLessonsAllCourses = 0;

    const coursesData = enrollments.map((enroll) => {
      const courseId = enroll.course.id;
      const totalItems = totalLessonsMap.get(courseId) || 0;
      const completedItems = completedItemsMap.get(courseId) || 0;
      const percentage =
        totalItems === 0
          ? 0
          : Math.round((completedItems / totalItems) * 100);

      totalCompletedLessonsAllCourses += completedItems;

      return {
        courseId: courseId,
        title: enroll.course.title,
        thumbnailUrl: enroll.course.thumbnailUrl,
        instructorName: enroll.course.instructor.fullName,
        totalLessons: totalItems,
        completedLessons: completedItems,
        percentage,
      };
    });

    const totalTimeResult = await this.prisma.lessonProgress.aggregate({
      where: { userId },
      _sum: { timeSpentSeconds: true },
    });

    const totalSeconds = totalTimeResult._sum.timeSpentSeconds || 0;
    const hoursSpent = parseFloat((totalSeconds / 3600).toFixed(1));

    return {
      studentName: user.fullName,
      hoursSpent: hoursSpent,
      badgesEarned: Math.floor(totalCompletedLessonsAllCourses / 5),
      courses: coursesData,
    };
  }

  private async createCourseCompletedNotification(params: {
    userId: string;
    lessonId: string;
    courseId: string;
    courseTitle: string;
  }) {
    try {
      const courseProgress = await this.getCourseProgress(
        params.userId,
        params.courseId,
      );

      if (courseProgress.percentage !== 100) {
        return;
      }

      const existingNotification = await this.prisma.notification.findFirst({
        where: {
          recipientId: params.userId,
          type: NotificationType.COURSE_COMPLETED,
          metadata: {
            path: ['courseId'],
            equals: params.courseId,
          },
        },
        select: { id: true },
      });

      if (existingNotification) {
        return;
      }

      await this.notificationsService.createNotification({
        recipientId: params.userId,
        type: NotificationType.COURSE_COMPLETED,
        title: 'Course completed',
        message: `You completed ${params.courseTitle}.`,
        metadata: {
          courseId: params.courseId,
          courseTitle: params.courseTitle,
          lessonId: params.lessonId,
        },
      });
    } catch (error) {
      console.error('Failed to create course completion notification', error);
    }
  }

  async generateCertificate(userId: string, courseId: string): Promise<Buffer> {
    const [user, course, progress] = await Promise.all([
      this.prisma.user.findUnique({
        where: { id: userId },
        select: { fullName: true },
      }),
      this.prisma.course.findUnique({
        where: { id: courseId },
        select: { title: true },
      }),
      this.getCourseProgress(userId, courseId),
    ]);

    if (!user || !course) {
      throw new NotFoundException('User or Course not found');
    }

    if (progress.percentage < 100) {
      throw new BadRequestException('Course must be 100% completed to receive a certificate.');
    }

    return new Promise((resolve, reject) => {
      try {
        const doc = new PDFDocument({
          layout: 'landscape',
          size: 'A4',
          margin: 50,
        });

        const buffers: Buffer[] = [];
        doc.on('data', (chunk) => buffers.push(chunk));
        doc.on('end', () => resolve(Buffer.concat(buffers)));
        doc.on('error', (err) => reject(err));

        // --- Design ---
        const pageWidth = doc.page.width;
        const pageHeight = doc.page.height;

        // Border
        doc.rect(20, 20, pageWidth - 40, pageHeight - 40)
           .lineWidth(5)
           .strokeColor('#FF9800')
           .stroke();

        doc.rect(30, 30, pageWidth - 60, pageHeight - 60)
           .lineWidth(1)
           .strokeColor('#FFB74D')
           .stroke();

        // Logo / Title
        doc.fillColor('#FF9800')
           .fontSize(40)
           .font('Helvetica-Bold')
           .text('SkillForge', 0, 80, { align: 'center', width: pageWidth });

        doc.fillColor('#333333')
           .fontSize(20)
           .font('Helvetica')
           .text('CERTIFICATE OF COMPLETION', 0, 150, { align: 'center', width: pageWidth });

        doc.fontSize(16)
           .text('This is to certify that', 0, 210, { align: 'center', width: pageWidth });

        // Student Name
        doc.fillColor('#FF9800')
           .fontSize(36)
           .font('Helvetica-Bold')
           .text(user.fullName, 0, 250, { align: 'center', width: pageWidth });

        doc.fillColor('#333333')
           .fontSize(16)
           .font('Helvetica')
           .text('has successfully completed the course', 0, 310, { align: 'center', width: pageWidth });

        // Course Title
        doc.fillColor('#000000')
           .fontSize(24)
           .font('Helvetica-Bold')
           .text(course.title, 0, 350, { align: 'center', width: pageWidth });

        // Date
        const dateStr = new Date().toLocaleDateString('en-US', {
          year: 'numeric',
          month: 'long',
          day: 'numeric',
        });
        doc.fillColor('#666666')
           .fontSize(14)
           .font('Helvetica')
           .text(`Issued on ${dateStr}`, 0, 420, { align: 'center', width: pageWidth });

        // Signature area
        doc.moveTo(pageWidth / 2 - 100, 500)
           .lineTo(pageWidth / 2 + 100, 500)
           .stroke();
        doc.fontSize(12)
           .text('SkillForge Team', 0, 510, { align: 'center', width: pageWidth });

        doc.end();
      } catch (err) {
        reject(err);
      }
    });
  }
}
