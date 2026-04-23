package com.example.skillforge.domain.repository

interface ReviewRepository {
    suspend fun submitReview(token: String, courseId: String, rating: Int, content: String): Result<Unit>
}