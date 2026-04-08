package com.example.skillforge.domain.model

data class ActiveCourse(
    val courseId: String,
    val title: String,
    val instructorName: String,
    val thumbnailUrl: String?,
    val totalLessons: Int,
    val completedLessons: Int,
    val percentage: Int
)