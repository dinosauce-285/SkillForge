package com.example.skillforge.domain.usecase

import com.example.skillforge.domain.model.User
import com.example.skillforge.domain.repository.UserRepository

class GetProfileUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<User> {
        return repository.getProfile()
    }
}