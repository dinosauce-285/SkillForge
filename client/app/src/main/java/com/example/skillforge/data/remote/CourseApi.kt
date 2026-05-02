package com.example.skillforge.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query // translated comment

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
    val status: String,
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
    val id: String,
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
    val quizzes: List<com.example.skillforge.data.remote.dto.QuizDto>? = emptyList()
)

data class MaterialDto(
    val id: String,
    val title: String,
    val type: String, // e.g., "VIDEO", "PDF", "DOCUMENT"
    val url: String? = null
)

data class CourseLessonDto(
    val id: String,
    val title: String,
)

data class EnrollmentStatusDto(
    val isEnrolled: Boolean
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
    @GET("courses")
    suspend fun getCourses(
        @Query("search") searchQuery: String? = null,
        @Query("categoryId") categoryId: String? = null,
        @Query("level") level: String? = null,
    ): Response<CourseListResponse>

    @GET("courses/suggested/recommendations")
    suspend fun getCourseSuggestions(): Response<List<CourseSummaryDto>>

    @GET("courses/{id}")
    suspend fun getCourseDetails(@Path("id") courseId: String): Response<CourseDetailsDto>

    @GET("courses/{id}/enrollment-status")
    suspend fun getEnrollmentStatus(
        @Path("id") courseId: String,
        @Header("Authorization") token: String
    ): Response<EnrollmentStatusDto>

    @Multipart
    @POST("courses")
    suspend fun createCourse(
        @Header("Authorization") token: String,
        @PartMap textFields: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part thumbnail: MultipartBody.Part?
    ): Response<CourseSummaryDto>

    @GET("courses/{id}/manager")
    suspend fun getCourseForManager(
        @Path("id") courseId: String,
        @Header("Authorization") token: String
    ): Response<CourseManagerDto>

    @GET("courses/instructor/my-courses")
    suspend fun getMyCourses(
        @Header("Authorization") token: String
    ): Response<List<CourseSummaryDto>>

    @GET("courses/{id}/students")
    suspend fun getCourseStudents(
        @Path("id") courseId: String,
        @Header("Authorization") token: String
    ): Response<List<CourseStudentDto>>
}

data class CourseStudentDto(
    val studentId: String,
    val fullName: String,
    val email: String,
    val avatarUrl: String?,
    val progressPercentage: Int,
    val enrolledAt: String
)
