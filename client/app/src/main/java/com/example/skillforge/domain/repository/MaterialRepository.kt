package com.example.skillforge.domain.repository

import java.io.File

interface MaterialRepository {
    suspend fun uploadMaterial(
        token: String,
        lessonId: String,
        title: String,
        type: String,
        file: File
    ): Result<Unit>
}