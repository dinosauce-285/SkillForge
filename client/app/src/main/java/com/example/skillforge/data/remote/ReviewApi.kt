package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class ReviewRequest(val rating: Int, val content: String)

interface ReviewApi {
    @POST("courses/{courseId}/reviews")
    suspend fun submitReview(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: String,
        @Body request: ReviewRequest
    ): Response<Unit>
}