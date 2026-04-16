package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.ReviewApi
import com.example.skillforge.data.remote.ReviewRequest
import com.example.skillforge.domain.repository.ReviewRepository

class ReviewRepositoryImpl(private val api: ReviewApi) : ReviewRepository {
    override suspend fun submitReview(token: String, courseId: String, rating: Int, content: String): Result<Unit> {
        return try {
            val request = ReviewRequest(rating, content)
            val response = api.submitReview("Bearer $token", courseId, request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to submit review: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}