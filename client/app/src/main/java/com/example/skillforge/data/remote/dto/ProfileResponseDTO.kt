package com.example.skillforge.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProfileResponseDTO(
    @SerializedName("id")
    val id: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("fullName")
    val fullName: String?,

    @SerializedName("profile")
    val profile: ProfileDetailDTO?
)

data class ProfileDetailDTO(
    @SerializedName("avatarUrl")
    val avatarUrl: String?,

    @SerializedName("skills")
    val skills: List<String>?,

    @SerializedName("learningGoals")
    val learningGoals: String?
)