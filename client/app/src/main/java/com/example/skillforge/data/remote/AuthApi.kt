package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)

// Thêm class này để hứng cục "user"
data class UserInfo(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String
)

// Cập nhật lại LoginResponse cho khớp 100% với JSON của server
data class LoginResponse(
    val message: String,
    val accessToken: String, // Đổi từ token -> accessToken
    val user: UserInfo       // Hứng luôn thông tin user
)

interface AuthApi {
    @POST("auth/login") // TODO: Nhớ check lại endpoint của bạn (vd: /auth/login, /api/login...)
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}