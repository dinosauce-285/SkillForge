package com.example.skillforge.domain.repository

import com.example.skillforge.data.remote.CreateLessonRequest
import com.example.skillforge.data.remote.LessonDto

interface LessonRepository {
    suspend fun createLesson(token: String, request: CreateLessonRequest): Result<LessonDto>
}