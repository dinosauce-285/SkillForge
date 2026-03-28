package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.AuthApi
import com.example.skillforge.data.remote.LoginRequest
import com.example.skillforge.domain.model.AuthSession
import com.example.skillforge.domain.model.AuthUser
import com.example.skillforge.domain.repository.AuthRepository
import com.google.gson.Gson
import com.google.gson.JsonObject

class AuthRepositoryImpl(private val api: AuthApi) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<AuthSession> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                Result.success(
                    AuthSession(
                        accessToken = data.accessToken,
                        user = AuthUser(
                            id = data.user.id,
                            email = data.user.email,
                            fullName = data.user.fullName,
                            role = data.user.role,
                        ),
                    )
                )
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Không thể kết nối tới máy chủ"))
        }
    }

    private fun parseErrorMessage(rawError: String?): String {
        if (rawError.isNullOrBlank()) {
            return "Đăng nhập thất bại"
        }

        return runCatching {
            val json = Gson().fromJson(rawError, JsonObject::class.java)
            when {
                json.has("message") && json.get("message").isJsonArray -> {
                    json.getAsJsonArray("message").joinToString(", ") { it.asString }
                }
                json.has("message") -> json.get("message").asString
                else -> "Đăng nhập thất bại"
            }
        }.getOrElse { "Đăng nhập thất bại" }
    }
}
