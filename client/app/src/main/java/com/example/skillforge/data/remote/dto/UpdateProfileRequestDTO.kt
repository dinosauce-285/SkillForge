package com.example.skillforge.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequestDTO(
    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @SerializedName("skills")
    val skills: List<String>? = null,

    @SerializedName("learningGoals")
    val learningGoals: String? = null
)