package com.example.skillforge.domain.repository

interface ReviewRepository {
    suspend fun submitReview(token: String, courseId: String, rating: Int, content: String): Result<Unit>
    suspend fun getCourseReviews(courseId: String): Result<com.example.skillforge.data.remote.CourseReviewsResponse>
}