package com.example.skillforge.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query // Import thêm cái này để dùng @Query

data class CourseListResponse(
    val data: List<CourseSummaryDto>,
)

data class CourseSummaryDto(
    val id: String,
    val title: String,
    val subtitle: String?,
    val summary: String?,
    val thumbnailUrl: String?,
    val price: Double,
    val isFree: Boolean,
    val level: String,
    val averageRating: Float,
    val studentCount: Int,
    val category: CourseCategoryDto,
    val instructor: CourseInstructorDto,
    val tags: List<CourseTagDto>,
    @SerializedName("_count") val counts: CourseCountDto,
)

data class CourseDetailsDto(
    val id: String,
    val title: String,
    val subtitle: String?,
    val summary: String?,
    val thumbnailUrl: String?,
    val price: Double,
    val isFree: Boolean,
    val level: String,
    val averageRating: Float,
    val studentCount: Int,
    val category: CourseCategoryDto,
    val instructor: CourseInstructorDto,
    val tags: List<CourseTagDto>,
    val chapters: List<CourseChapterDto>,
    @SerializedName("_count") val counts: CourseCountDto,
)

data class CourseCategoryDto(
    val name: String,
)

data class CourseInstructorDto(
    val fullName: String,
    val profile: CourseInstructorProfileDto?,
)

data class CourseInstructorProfileDto(
    val skills: List<String>?,
    val learningGoals: String?,
)

data class CourseTagDto(
    val name: String,
)

data class CourseCountDto(
    val chapters: Int,
    val reviews: Int,
    val enrollments: Int,
)

data class CourseChapterDto(
    val id: String,
    val title: String,
    val lessons: List<CourseLessonDto>,
)

data class CourseLessonDto(
    val title: String,
)

data class CreateCourseRequest(
    val title: String,
    val summary: String,
    val price: Double,
    val categoryId: String,
    val thumbnailUrl: String? = null
)

data class CreateCourseResponse(
    val message: String,
    val data: CourseSummaryDto
)

interface CourseApi {
    // Đã thêm searchQuery và categoryId để khớp với Interface và Repository
    @GET("courses")
    suspend fun getCourses(
        @Query("search") searchQuery: String? = null,
        @Query("categoryId") categoryId: String? = null
    ): Response<CourseListResponse>

    @GET("courses/{id}")
    suspend fun getCourseDetails(@Path("id") courseId: String): Response<CourseDetailsDto>

    @POST("courses")
    suspend fun createCourse(
        @Header("Authorization") token: String,
        @Body request: CreateCourseRequest
    ): Response<CreateCourseResponse>

}