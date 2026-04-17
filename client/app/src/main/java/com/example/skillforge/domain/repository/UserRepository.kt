package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.User

interface UserRepository {
    suspend fun getProfile(): Result<User>

    suspend fun updateProfile(
        fullName: String? = null,
        avatarUrl: String? = null,
        skills: List<String>? = null,
        learningGoals: String? = null
    ): Result<User>

    suspend fun uploadAvatar(
        imageBytes: ByteArray,
        fileName: String
    ): Result<String>
}
