package com.example.skillforge.domain.repository

import com.example.skillforge.data.remote.InstructorDashboardDto

interface DashboardRepository {
    suspend fun getInstructorDashboard(token: String): Result<InstructorDashboardDto>
}