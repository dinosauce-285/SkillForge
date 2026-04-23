import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class ProgressService {
  constructor(private readonly prisma: PrismaService) {}

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
    });

    if (!lesson) {
      throw new NotFoundException(`Lesson with id "${lessonId}" not found.`);
    }

    return await this.prisma.lessonProgress.upsert({
      where: {
        userId_lessonId: { userId, lessonId },
      },
      update: { isCompleted },
      create: { userId, lessonId, isCompleted },
    });
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
              },
            },
          },
        },
      },
    });

    if (!course) {
      throw new NotFoundException(`Course with id "${courseId}" not found`);
    }

    // Sum lesson
    const totalLessons = course.chapters.reduce(
      (sum, chap) => sum + chap._count.lessons,
      0,
    );

    // count completed lessons
    const completedLessons = await this.prisma.lessonProgress.count({
      where: {
        userId,
        isCompleted: true,
        lesson: {
          chapter: {
            courseId: courseId,
          },
        },
      },
    });

    // Calculate percent
    const percentage =
      totalLessons === 0
        ? 0
        : Math.round((completedLessons / totalLessons) * 100);

    return {
      courseId,
      totalLessons,
      completedLessons,
      percentage,
    };
  }

  async getDashboardData(userId: string) {
<<<<<<< feat/quizbuilder-api
    console.log(userId);
=======
>>>>>>> dev
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
            _count: { select: { lessons: { where: { deletedAt: null } } } },
          },
        },
      },
    });

    const totalLessonsMap = new Map<string, number>();
    coursesWithLessons.forEach((course) => {
      const total = course.chapters.reduce(
        (sum, chap) => sum + chap._count.lessons,
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

    const completedLessonsMap = new Map<string, number>();
    completedLessonsData.forEach((progress) => {
      const cId = progress.lesson.chapter.courseId;
      completedLessonsMap.set(cId, (completedLessonsMap.get(cId) || 0) + 1);
    });

    let totalCompletedLessonsAllCourses = 0;

    const coursesData = enrollments.map((enroll) => {
      const courseId = enroll.course.id;
      const totalLessons = totalLessonsMap.get(courseId) || 0;
      const completedLessons = completedLessonsMap.get(courseId) || 0;
      const percentage =
        totalLessons === 0
          ? 0
          : Math.round((completedLessons / totalLessons) * 100);

      totalCompletedLessonsAllCourses += completedLessons;

      return {
        courseId: courseId,
        title: enroll.course.title,
        thumbnailUrl: enroll.course.thumbnailUrl,
        instructorName: enroll.course.instructor.fullName,
        totalLessons,
        completedLessons,
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
}
