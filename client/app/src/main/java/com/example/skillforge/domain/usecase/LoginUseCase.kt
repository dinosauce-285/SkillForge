package com.example.skillforge.domain.usecase
import com.example.skillforge.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        // Có thể check validate email/pass ở đây trước khi gọi repo
        if(email.isBlank() || password.isBlank()) return Result.failure(Exception("Vui lòng nhập đủ thông tin"))
        return repository.login(email, password)
    }
}