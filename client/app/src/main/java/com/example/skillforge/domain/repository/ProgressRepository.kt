package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.HomeDashboard
import com.example.skillforge.data.remote.CourseProgressDto

interface ProgressRepository {
    suspend fun getDashboardProgress(token: String): HomeDashboard
    suspend fun getCourseProgress(courseId: String): CourseProgressDto
}