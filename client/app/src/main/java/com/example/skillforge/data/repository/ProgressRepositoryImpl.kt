package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.ProgressApi
import com.example.skillforge.domain.model.HomeDashboard
import com.example.skillforge.domain.repository.ProgressRepository

class ProgressRepositoryImpl(
    private val progressApi: ProgressApi
) : ProgressRepository {

    /**
     * Fetches the dashboard data from the backend.
     * The token is handled automatically by the OkHttp Interceptor.
     */
    override suspend fun getDashboardProgress(token: String): HomeDashboard {
        // We ignore the token parameter here as the Interceptor provides it globally
        return progressApi.getDashboardProgress()
    }

    override suspend fun getCourseProgress(courseId: String): com.example.skillforge.data.remote.CourseProgressDto {
        return progressApi.getCourseProgress(courseId)
    }

    override suspend fun markLessonCompleted(lessonId: String, isCompleted: Boolean): Result<Unit> {
        return try {
            val response = progressApi.markLessonCompleted(lessonId, com.example.skillforge.data.remote.MarkLessonRequest(isCompleted))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark lesson: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
