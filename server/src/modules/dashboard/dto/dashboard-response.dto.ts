export class DashboardStatsDto {
  totalStudents: number;
  activeCourses: number;
  totalEarnings: number;
}

export class ChartDataDto {
  month: string;
  count: number;
}

export class DashboardResponseDto {
  stats: DashboardStatsDto;
  chartData: ChartDataDto[];
}
