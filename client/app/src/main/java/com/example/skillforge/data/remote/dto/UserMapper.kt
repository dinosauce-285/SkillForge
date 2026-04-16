package com.example.skillforge.data.mapper

import com.example.skillforge.data.remote.dto.ProfileResponseDTO
import com.example.skillforge.domain.model.User
import com.example.skillforge.domain.model.UserProfile

fun ProfileResponseDTO.toDomain(): User {
    return User(
        id = this.id.orEmpty(),
        email = this.email.orEmpty(),
        fullName = this.fullName.orEmpty(),
        profile = this.profile?.let { profileDto ->
            UserProfile(
                avatarUrl = profileDto.avatarUrl,
                skills = profileDto.skills ?: emptyList(),
                learningGoals = profileDto.learningGoals
            )
        }
    )
}