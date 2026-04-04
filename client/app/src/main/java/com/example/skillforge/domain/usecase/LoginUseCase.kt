package com.example.skillforge.domain.usecase

import com.example.skillforge.domain.model.AuthSession
import com.example.skillforge.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<AuthSession> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Please enter both email and password"))
        }

        return repository.login(email, password)
    }

    suspend fun loginWithGoogle() {
        repository.loginWithGoogle()
    }
}
