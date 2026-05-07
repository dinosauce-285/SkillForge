package com.example.skillforge.data.remote

import com.example.skillforge.domain.model.HomeDashboard
import retrofit2.http.GET
import retrofit2.http.Path

data class QuizProgressDto(
    val quizId: String,
    val attemptId: String,
    val isPassed: Boolean,
    val status: String
)

data class CourseProgressDto(
    val courseId: String,
    val totalLessons: Int,
    val completedLessons: Int,
    val percentage: Int,
    val completedLessonIds: List<String>?,
    val completedQuizIds: List<String>? = emptyList(),
    val quizStatuses: List<QuizProgressDto>? = emptyList()
)

interface ProgressApi {
    /**
     * Fetches dashboard data. 
     * Authorization header is automatically handled by OkHttp Interceptor.
     */
    @GET("progress/dashboard")
    suspend fun getDashboardProgress(): HomeDashboard

    @GET("progress/courses/{courseId}")
    suspend fun getCourseProgress(@Path("courseId") courseId: String): CourseProgressDto
}
