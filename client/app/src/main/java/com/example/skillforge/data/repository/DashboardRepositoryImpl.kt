package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.DashboardApi
import com.example.skillforge.data.remote.InstructorDashboardDto
import com.example.skillforge.domain.repository.DashboardRepository

class DashboardRepositoryImpl(
    private val api: DashboardApi
) : DashboardRepository {
    override suspend fun getInstructorDashboard(token: String): Result<InstructorDashboardDto> {
        return try {
            val response = api.getInstructorDashboard("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch dashboard data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}