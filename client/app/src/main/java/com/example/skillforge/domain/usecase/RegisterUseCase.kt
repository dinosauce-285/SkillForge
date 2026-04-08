package com.example.skillforge.domain.usecase

import com.example.skillforge.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(fullName: String, email: String, password: String): Result<String> {
        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
            return Result.failure(Exception("Please fill in all required fields"))
        }
        return repository.register(fullName, email, password)
    }

    suspend fun loginWithGoogle() {
        repository.loginWithGoogle()
    }
}
