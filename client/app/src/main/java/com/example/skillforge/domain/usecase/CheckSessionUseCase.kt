package com.example.skillforge.domain.usecase

import com.example.skillforge.domain.model.AuthSession
import com.example.skillforge.domain.repository.AuthRepository

class CheckSessionUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<AuthSession> {
        return repository.verifySession()
    }
}
