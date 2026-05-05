package com.example.skillforge.data.remote

import com.google.gson.annotations.SerializedName

// ---- User DTOs ----
data class AdminUserDto(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: String? = null,
    val profile: AdminUserProfileDto? = null
)

data class AdminUserProfileDto(
    val avatarUrl: String? = null,
    val skills: List<String>? = null,
    val learningGoals: String? = null
)

// ---- Course Queue DTOs ----
// Shape: GET /admin/courses/queue -> List<AdminCourseQueueDto>
// Backend returns flat course with instructor & category nested
data class AdminCourseQueueDto(
    val id: String,
    val title: String,
    val level: String? = null,
    val status: String? = null,
    val instructor: AdminInstructorDto? = null,
    val category: AdminCategoryDto? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class AdminInstructorDto(
    val id: String,
    val fullName: String,
    val email: String
)

data class AdminCategoryDto(
    val id: String,
    val name: String
)

// ---- Course Preview DTOs ----
// Shape: GET /admin/courses/:id/preview
// Backend returns a FLAT object: { id, title, level, status, instructor, category, chapters: [...] }
data class AdminCoursePreviewDto(
    val id: String,
    val title: String,
    val level: String? = null,
    val status: String? = null,
    val instructor: AdminInstructorDto? = null,
    val category: AdminCategoryDto? = null,
    val chapters: List<AdminChapterDto>? = null
)

data class AdminChapterDto(
    val id: String,
    val title: String,
    val orderIndex: Int? = null,
    val lessons: List<AdminLessonDto>? = null,
    val quizzes: List<AdminQuizDto>? = null
)

data class AdminLessonDto(
    val id: String,
    val title: String,
    val orderIndex: Int? = null,
    val materials: List<AdminMaterialDto>? = null
)

data class AdminMaterialDto(
    val id: String,
    val title: String,
    val type: String? = null,
    val url: String? = null
)

data class AdminQuizDto(
    val id: String,
    val title: String
)
