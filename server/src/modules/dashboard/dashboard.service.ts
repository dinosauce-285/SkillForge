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
      const chartDataRaw: { month: string; year: number; count: number }[] = [];

      for (let i = 5; i >= 0; i--) {
        const d = new Date();
        d.setDate(1); // Set to 1st to prevent month skipping
        d.setMonth(d.getMonth() - i);
        chartDataRaw.push({
          month: monthNames[d.getMonth()],
          year: d.getFullYear(),
          count: 0,
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

      // 5. Format to match the DTO expected by Android
      const chartData = chartDataRaw.map((c) => {
        // Generate mock revenue based on count
        const fakeRevenue = c.count === 0 ? Math.random() * 50 : c.count * (100 + Math.random() * 50);
        return {
          month: c.month,
          count: c.count,
          revenue: parseFloat(fakeRevenue.toFixed(2))
        }
      });

      return {
        stats: {
          totalStudents,
          activeCourses,
          totalEarnings: chartData.reduce((acc, obj) => acc + obj.revenue, 0),
          passRate: 75,
          failRate: 25
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
