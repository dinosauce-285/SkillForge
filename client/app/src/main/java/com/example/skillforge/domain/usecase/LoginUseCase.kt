package com.example.skillforge.domain.usecase

import com.example.skillforge.domain.model.AuthSession
import com.example.skillforge.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<AuthSession> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Vui lòng nhập đủ email và mật khẩu"))
        }

        return repository.login(email, password)
    }
}
