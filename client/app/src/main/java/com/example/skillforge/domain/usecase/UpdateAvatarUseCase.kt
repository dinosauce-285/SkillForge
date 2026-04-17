package com.example.skillforge.domain.usecase

import com.example.skillforge.domain.repository.UserRepository

class UpdateAvatarUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        imageBytes: ByteArray,
        fileName: String
    ): Result<String> {
        return repository.uploadAvatar(imageBytes, fileName)
    }
}