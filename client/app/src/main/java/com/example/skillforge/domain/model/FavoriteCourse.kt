package com.example.skillforge.domain.model

data class FavoriteCourse(
    val id: String,
    val title: String,
    val instructorName: String,
    val price: Double,
    val isFree: Boolean,
    val thumbnailUrl: String? = null,
)
