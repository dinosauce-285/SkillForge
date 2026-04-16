package com.example.skillforge.domain.usecase

import com.example.skillforge.domain.model.User
import com.example.skillforge.domain.repository.UserRepository
import com.example.skillforge.data.remote.dto.UpdateProfileRequestDTO

class UpdateProfileUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        token: String,
        request: UpdateProfileRequestDTO
    ): Result<User> {
        return repository.updateProfile(
            token = token,
            fullName = request.fullName,
            avatarUrl = request.avatarUrl,
            skills = request.skills,
            learningGoals = request.learningGoals
        )
    }
}
