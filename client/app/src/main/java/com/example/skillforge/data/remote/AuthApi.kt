package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)

data class UserInfo(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String
)

data class LoginResponse(
    val message: String,
    val accessToken: String,
    val refreshToken: String,
    val user: UserInfo
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String
)

data class RegisterResponse(
    val message: String,
    val user: UserInfo       // translated comment
)

interface AuthApi {
    @GET("auth/me")
    suspend fun getMe(): Response<UserInfo>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}
