package com.example.skillforge.domain.repository

import com.example.skillforge.data.remote.ChapterDto
import com.example.skillforge.data.remote.CreateChapterRequest

interface ChapterRepository {
    suspend fun createChapter(token: String, request: CreateChapterRequest): Result<ChapterDto>
}