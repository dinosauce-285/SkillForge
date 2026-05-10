package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class ReviewRequest(val rating: Int, val content: String)

data class ReviewResponse(
    val id: String,
    val studentId: String,
    val rating: Int,
    val content: String,
    val createdAt: String,
    val student: ReviewStudentDto
)

data class ReviewStudentDto(
    val fullName: String
)

data class CourseReviewsResponse(
    val averageRating: Float,
    val totalReviews: Int,
    val reviews: List<ReviewResponse>
)

interface ReviewApi {
    @POST("courses/{courseId}/reviews")
    suspend fun submitReview(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: String,
        @Body request: ReviewRequest
    ): Response<Unit>

    @GET("courses/{courseId}/reviews")
    suspend fun getReviews(
        @Path("courseId") courseId: String
    ): Response<CourseReviewsResponse>
}