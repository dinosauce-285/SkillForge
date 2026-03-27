package com.example.skillforge.domain.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<String> // Trả về Token hoặc Lỗi
}