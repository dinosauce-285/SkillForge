package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DashboardApi {
    @GET("dashboard/instructor")
    suspend fun getInstructorDashboard(
        @Header("Authorization") token: String
    ): Response<InstructorDashboardDto>
}

data class DashboardStatsDto(
    val totalStudents: Int,
    val activeCourses: Int,
    val totalEarnings: Double,
    val passRate: Float,
    val failRate: Float
)

data class ChartDataDto(
    val month: String,
    val count: Int,
    val revenue: Double
)

data class InstructorDashboardDto(
    val stats: DashboardStatsDto,
    val chartData: List<ChartDataDto>
)

// Add this so old code doesn't crash, even if we aren't using it heavily yet
data class InstructorAnalyticsDto(
    val totalRevenue: Double,
    val revenueGrowth: Double,
    val newEnrollments: Int,
    val enrollmentsGrowth: Double,
    val studentSatisfaction: Double,
    val satisfactionRank: String
)