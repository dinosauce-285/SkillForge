package com.example.skillforge.domain.usecase

import com.example.skillforge.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(fullName: String, email: String, password: String): Result<String> {
        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
            return Result.failure(Exception("Vui lòng nhập đủ thông tin"))
        }
        return repository.register(fullName, email, password)
    }
}
