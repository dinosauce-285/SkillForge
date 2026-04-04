package com.example.skillforge.domain.model

data class LessonContent(
    val id: String,
    val title: String,
    val chapterTitle: String,
    val courseTitle: String,
    val materials: List<LessonMaterial>,
)

data class LessonMaterial(
    val id: String,
    val type: String,
    val fileUrl: String,
    val fileSize: Int,
    val status: String,
)
