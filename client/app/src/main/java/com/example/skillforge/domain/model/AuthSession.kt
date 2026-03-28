package com.example.skillforge.domain.model

data class AuthUser(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
)

data class AuthSession(
    val accessToken: String,
    val user: AuthUser,
)
