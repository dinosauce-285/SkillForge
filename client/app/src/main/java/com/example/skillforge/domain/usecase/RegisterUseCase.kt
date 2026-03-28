package com.example.skillforge.domain.usecase
import com.example.skillforge.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, fullName: String): Result<String> {
        // Có thể check validate email/pass ở đây trước khi gọi repo
        if(email.isBlank() || password.isBlank() || fullName.isBlank()) return Result.failure(Exception("Vui lòng nhập đủ thông tin"))
        return repository.register(fullName, email, password)
    }
}