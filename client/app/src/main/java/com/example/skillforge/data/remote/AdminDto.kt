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

// ---- Platform Coupon DTOs ----
data class AdminPlatformCouponDto(
    val id: String,
    val code: String,
    val discountPercent: Int,
    val scope: String? = null,
    val instructorId: String? = null,
    val isActive: Boolean,
    val createdAt: String? = null
)

data class CreatePlatformCouponRequest(
    val code: String,
    val discountPercent: Int,
    val isActive: Boolean = true
)

data class UpdatePlatformCouponRequest(
    val code: String? = null,
    val discountPercent: Int? = null,
    val isActive: Boolean? = null
)

// ---- Finance DTOs ----
data class AdminFinanceSummaryDto(
    val grossRevenue: Double,
    val netPlatformRevenue: Double,
    val pendingInstructorBalance: Double,
    val availableInstructorBalance: Double
)

data class AdminFinanceSnapshotListDto(
    val data: List<AdminFinanceSnapshotDto>,
    val meta: AdminPaginationDto
)

data class AdminPaginationDto(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

data class AdminFinanceSnapshotDto(
    val id: String,
    val orderId: String,
    val orderStatus: String,
    val orderCreatedAt: String? = null,
    val courseId: String,
    val courseTitle: String,
    val instructorId: String,
    val originalCoursePrice: Double,
    val customerPaidAmount: Double,
    val couponId: String? = null,
    val couponCode: String? = null,
    val couponScope: String? = null,
    val discountAmount: Double,
    val discountAbsorbedByPlatform: Double,
    val discountAbsorbedByInstructor: Double,
    val platformShareRate: Int,
    val instructorShareRate: Int,
    val instructorGrossRevenue: Double,
    val instructorNetRevenue: Double,
    val platformGrossRevenue: Double,
    val platformNetRevenue: Double,
    val pendingReleaseDate: String? = null,
    val createdAt: String? = null
)
