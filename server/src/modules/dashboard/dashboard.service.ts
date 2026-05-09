import { Injectable, InternalServerErrorException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class DashboardService {
  constructor(private prisma: PrismaService) {}

  async getInstructorDashboard(instructorId: string) {
    try {
      const totalStudents = await this.prisma.enrollment.count({
        where: { course: { instructorId: instructorId } },
      });

      const activeCourses = await this.prisma.course.count({
        where: { instructorId: instructorId, status: 'PUBLISHED' },
      });

      // 1. Calculate the exact date for 6 months ago (starting from the 1st day)
      const sixMonthsAgo = new Date();
      sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 5);
      sixMonthsAgo.setDate(1);
      sixMonthsAgo.setHours(0, 0, 0, 0);

      // 2. Fetch all enrollments securely using Prisma (Using 'enrolledAt' instead of 'createdAt')
      const recentEnrollments = await this.prisma.enrollment.findMany({
        where: {
          course: { instructorId: instructorId },
          enrolledAt: { gte: sixMonthsAgo },
        },
        select: { enrolledAt: true },
      });

      // 2.5 Fetch all financial snapshots for the last 6 months for chart revenue
      const recentFinancials = await this.prisma.orderFinancialSnapshot.findMany({
        where: {
          order: { course: { instructorId: instructorId } },
          createdAt: { gte: sixMonthsAgo },
        },
        select: { createdAt: true, instructorNetRevenue: true },
      });

      // 2.6 Fetch lifetime earnings
      const lifetimeFinancials = await this.prisma.orderFinancialSnapshot.aggregate({
        where: { order: { course: { instructorId: instructorId } } },
        _sum: { instructorNetRevenue: true },
      });
      const lifetimeEarnings = lifetimeFinancials._sum?.instructorNetRevenue?.toNumber() || 0;

      // 2.7 Fetch Quiz Attempts to calculate pass/fail rate
      const quizAttempts = await this.prisma.quizAttempt.findMany({
        where: {
          quiz: { chapter: { course: { instructorId: instructorId } } },
          status: 'GRADED',
        },
        select: { isPassed: true },
      });

      let passRate = 0;
      let failRate = 0;
      if (quizAttempts.length > 0) {
        const passedCount = quizAttempts.filter((a) => a.isPassed).length;
        passRate = Math.round((passedCount / quizAttempts.length) * 100);
        failRate = 100 - passRate;
      }

      // 3. Initialize an empty array with explicit TypeScript types to fix the 'never[]' error
      const monthNames = [
        'Jan',
        'Feb',
        'Mar',
        'Apr',
        'May',
        'Jun',
        'Jul',
        'Aug',
        'Sep',
        'Oct',
        'Nov',
        'Dec',
      ];
      const chartDataRaw: { month: string; year: number; count: number; revenue: number }[] = [];

      for (let i = 5; i >= 0; i--) {
        const d = new Date();
        d.setDate(1); // Set to 1st to prevent month skipping
        d.setMonth(d.getMonth() - i);
        chartDataRaw.push({
          month: monthNames[d.getMonth()],
          year: d.getFullYear(),
          count: 0,
          revenue: 0,
        });
      }

      // 4. Populate the array with actual counts
      recentEnrollments.forEach((enrollment) => {
        const monthStr = monthNames[enrollment.enrolledAt.getMonth()];
        const yearNum = enrollment.enrolledAt.getFullYear();

        const targetMonth = chartDataRaw.find(
          (c) => c.month === monthStr && c.year === yearNum,
        );
        if (targetMonth) {
          targetMonth.count += 1;
        }
      });

      // 4.5 Populate the array with actual revenue
      recentFinancials.forEach((financial) => {
        const monthStr = monthNames[financial.createdAt.getMonth()];
        const yearNum = financial.createdAt.getFullYear();

        const targetMonth = chartDataRaw.find(
          (c) => c.month === monthStr && c.year === yearNum,
        );
        if (targetMonth) {
          targetMonth.revenue += financial.instructorNetRevenue.toNumber();
        }
      });

      // 5. Format to match the DTO expected by Android
      const chartData = chartDataRaw.map((c) => {
        return {
          month: c.month,
          count: c.count,
          revenue: parseFloat(c.revenue.toFixed(2))
        }
      });

      return {
        stats: {
          totalStudents,
          activeCourses,
          totalEarnings: lifetimeEarnings,
          passRate: passRate,
          failRate: failRate
        },
        chartData,
      };
    } catch (error) {
      console.error(error);
      throw new InternalServerErrorException(
        'Failed to generate dashboard statistics',
      );
    }
  }
}
