package com.example.skillforge.domain.model

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val isActive: Boolean = true,
    val profile: UserProfile?
)

data class UserProfile(
    val avatarUrl: String?,
    val skills: List<String>,
    val learningGoals: String?
)