package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val sessionFlow: Flow<AuthSession?>
    suspend fun login(email: String, password: String): Result<AuthSession>
    suspend fun register(fullName: String, email: String, password: String): Result<String>
    suspend fun loginWithGoogle()
}
