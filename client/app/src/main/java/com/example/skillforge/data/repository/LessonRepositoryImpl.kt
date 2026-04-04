package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.CreateLessonRequest
import com.example.skillforge.data.remote.LessonApi
import com.example.skillforge.domain.repository.LessonRepository
import com.example.skillforge.data.remote.LessonDto
import com.example.skillforge.domain.model.LessonContent
import com.example.skillforge.domain.model.LessonMaterial

class LessonRepositoryImpl(private val api: LessonApi) : LessonRepository {
    override suspend fun getLessonDetails(token: String, lessonId: String): Result<LessonContent> {
        return try {
            val response = api.getLessonDetails(lessonId, "Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(
                    LessonContent(
                        id = dto.id,
                        title = dto.title,
                        chapterTitle = dto.chapter.title,
                        courseTitle = dto.chapter.course.title,
                        materials = dto.materials.map { material ->
                            LessonMaterial(
                                id = material.id,
                                type = material.type.name,
                                fileUrl = material.fileUrl,
                                fileSize = material.fileSize,
                                status = material.status.name,
                            )
                        },
                    ),
                )
            } else {
                Result.failure(Exception("Failed to load lesson"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to load lesson"))
        }
    }

    override suspend fun createLesson(token: String, request: CreateLessonRequest): Result<LessonDto> {
        return try {
            val response = api.createLesson("Bearer $token", request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
