package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.AuthApi
import com.example.skillforge.data.remote.LoginRequest
import com.example.skillforge.data.remote.RegisterRequest
import com.example.skillforge.domain.repository.AuthRepository
import android.util.Log

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

    override suspend fun register(fullName: String, email: String, password: String): Result<String> {
        Log.d("API_DEBUG", "2. Đã chui vào AuthRepositoryImpl! Chuẩn bị gọi Server...")

        return try {
            val response = api.register(RegisterRequest(fullName, email, password))

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                Result.success(data.message)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Không có chi tiết"
                Result.failure(Exception("Server chê: Mã ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi Mạng/Crash: ${e.message}"))
        }
    }
}