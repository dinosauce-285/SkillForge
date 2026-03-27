package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.AuthApi
import com.example.skillforge.data.remote.LoginRequest
import com.example.skillforge.domain.repository.AuthRepository

class AuthRepositoryImpl(private val api: AuthApi) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                Result.success(data.accessToken)
            } else {
                Result.failure(Exception("Sai tài khoản hoặc mật khẩu"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối mạng: ${e.message}"))
        }
    }
}