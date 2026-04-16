package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.User

interface UserRepository {
    suspend fun getProfile(token: String): Result<User>

    suspend fun updateProfile(
        token: String,
        fullName: String? = null,
        avatarUrl: String? = null,
        skills: List<String>? = null,
        learningGoals: String? = null
    ): Result<User>
}