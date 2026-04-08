import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { NotFoundError } from 'rxjs';

@Injectable()
export class ProgressService {
    constructor(private readonly prisma: PrismaService) { }

    async markLessonCompleted(userId: string, lessonId: string, isCompleted: boolean = true) {
        // Kiểm tra xem lessonId có tồn tại k 
        const lesson = await this.prisma.lesson.findFirst({
            where: {
                id: lessonId,
                deletedAt: null // Bỏ qua nếu bài học đã bị xóa mềm
            }
        });

        if (!lesson) {
            // Ném ra lỗi 404 gọn gàng, NestJS sẽ tự bắt và trả về cho Frontend, KHÔNG sập server
            throw new NotFoundException(`Lesson với id "${lessonId}" không tồn tại hoặc đã bị xóa.`);
        }

        try {
            return await this.prisma.lessonProgress.upsert({
                where: {
                    userId_lessonId: { userId, lessonId },
                },
                update: { isCompleted },
                create: { userId, lessonId, isCompleted },
            });
        } catch (error) {
            throw error;
        }
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
                                    where: { deletedAt: null }
                                }
                            }
                        }
                    }
                }
            }
        })

        if (!course) {
            throw new NotFoundException(`Course with id "${courseId}" not found`);
        }

        // Sum lesson 
        const totalLessons = course.chapters.reduce((sum, chap) => sum + chap._count.lessons, 0);

        // count completed lessons 
        const completedLessons = await this.prisma.lessonProgress.count({
            where: {
                userId,
                isCompleted: true,
                lesson: {
                    chapter: {
                        courseId: courseId,
                    }
                }
            }
        });

        // Calculate percent
        const percentage = totalLessons === 0 ? 0 : Math.round((completedLessons / totalLessons) * 100);

        return {
            courseId,
            totalLessons,
            completedLessons,
            percentage,
        }
    }

    // Get % for all courses (for HomeDashboard)
    async getDashboardProgress(userId: string) {
        const enrollments = await this.prisma.enrollment.findMany({
            where: { userId, status: 'ACTIVE' },
            include: {
                course: {
                    select: { id: true, title: true, thumbnailUrl: true, instructor: { select: { fullName: true } } }
                }
            }
        });

        const dashboardData = await Promise.all(
            enrollments.map(async (enroll) => {
                const progress = await this.getCourseProgress(userId, enroll.courseId);
                return {
                    title: enroll.course.title,
                    thumbnailUrl: enroll.course.thumbnailUrl,
                    instructorName: enroll.course.instructor.fullName,
                    ...progress,
                };
            }),
        );

        return dashboardData;
    }
}
