package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.CreateLessonRequest
import com.example.skillforge.data.remote.LessonApi
import com.example.skillforge.domain.repository.LessonRepository
import com.example.skillforge.data.remote.LessonDto

class LessonRepositoryImpl(private val api: LessonApi) : LessonRepository {
    override suspend fun createLesson(token: String, request: CreateLessonRequest): Result<LessonDto> {
        return try {
            val response = api.createLesson("Bearer $token", request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}