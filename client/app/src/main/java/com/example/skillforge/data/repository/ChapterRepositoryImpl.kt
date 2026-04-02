package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.ChapterApi
import com.example.skillforge.data.remote.CreateChapterRequest
import com.example.skillforge.domain.repository.ChapterRepository
import com.example.skillforge.data.remote.ChapterDto

class ChapterRepositoryImpl(private val api: ChapterApi) : ChapterRepository {
    override suspend fun createChapter(token: String, request: CreateChapterRequest): Result<ChapterDto> {
        return try {
            val response = api.createChapter("Bearer $token", request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}