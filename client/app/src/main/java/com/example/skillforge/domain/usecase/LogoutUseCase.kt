package com.example.skillforge.domain.usecase

import com.example.skillforge.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
