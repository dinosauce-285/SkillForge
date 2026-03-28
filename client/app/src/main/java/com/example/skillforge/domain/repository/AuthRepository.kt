package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.AuthSession

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthSession>
}
