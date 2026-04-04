package com.example.skillforge.domain.model

data class CourseSummary(
    val id: String,
    val title: String,
    val subtitle: String?,
    val summary: String?,
    val thumbnailUrl: String? = null,
    val categoryId: String,
    val categoryName: String,
    val instructorName: String,
    val level: String,
    val price: Double,
    val isFree: Boolean,
    val averageRating: Float,
    val studentCount: Int,
    val reviewCount: Int,
    val chapterCount: Int,
    val tags: List<String>,
)

data class CourseChapter(
    val id: String,
    val title: String,
    val lessonTitles: List<String>,
)

data class CourseDetails(
    val id: String,
    val title: String,
    val subtitle: String?,
    val summary: String?,
    val thumbnailUrl: String? = null,
    val categoryName: String,
    val instructorName: String,
    val instructorSkills: List<String>,
    val instructorGoals: String?,
    val level: String,
    val price: Double,
    val isFree: Boolean,
    val averageRating: Float,
    val studentCount: Int,
    val reviewCount: Int,
    val chapterCount: Int,
    val tags: List<String>,
    val chapters: List<CourseChapter>,
)
