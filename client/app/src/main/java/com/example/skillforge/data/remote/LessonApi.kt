package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

enum class MaterialType { VIDEO, DOCUMENT, LINK, SOURCE_CODE }
enum class MaterialStatus { UPLOADING, PROCESSING, READY, FAILED }

data class LessonMaterialDto(
    val id: String,
    val lessonId: String,
    val type: MaterialType,
    val fileUrl: String,
    val fileSize: Int,
    val status: MaterialStatus
)

data class LessonDto(
    val id: String,
    val chapterId: String,
    val title: String,
    val orderIndex: Int,
    val materials: List<LessonMaterialDto> = emptyList()
)

data class LessonCourseDto(
    val title: String,
)

data class LessonChapterDto(
    val title: String,
    val course: LessonCourseDto,
)

data class LessonDetailsDto(
    val id: String,
    val title: String,
    val chapter: LessonChapterDto,
    val materials: List<LessonMaterialDto> = emptyList(),
)

data class CreateLessonRequest(
    val chapterId: String,
    val title: String,
    val orderIndex: Int? = null
)

data class UpdateLessonRequest(
    val title: String? = null,
    val orderIndex: Int? = null
)

interface LessonApi {
    @GET("lessons/{id}")
    suspend fun getLessonDetails(
        @Path("id") lessonId: String,
        @Header("Authorization") token: String,
    ): Response<LessonDetailsDto>

    @POST("lessons")
    suspend fun createLesson(
        @Header("Authorization") token: String,
        @Body request: CreateLessonRequest
    ): LessonDto

    @PATCH("lessons/{id}")
    suspend fun updateLesson(
        @Path("id") lessonId: String,
        @Header("Authorization") token: String,
        @Body request: UpdateLessonRequest
    ): LessonDto

    @DELETE("lessons/{id}")
    suspend fun deleteLesson(
        @Path("id") lessonId: String,
        @Header("Authorization") token: String
    )
}
