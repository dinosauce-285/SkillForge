package com.example.skillforge.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class ChapterDto(
    val id: String,
    val courseId: String,
    val title: String,
    val orderIndex: Int,

    val lessons: List<LessonDto> = emptyList()
)

data class CourseManagerDto(
    val id: String,
    val title: String,
    val category: CategoryDto?,

    val chapters: List<ChapterDto> = emptyList(),
    @SerializedName("_count")
    val count: CourseCount?
) {
    data class CourseCount(val chapters: Int, val enrollments: Int)
}

data class CreateChapterRequest(
    val courseId: String,
    val title: String,
    val orderIndex: Int? = null
)

data class UpdateChapterRequest(
    val title: String? = null,
    val orderIndex: Int? = null
)

interface ChapterApi {
    @POST("chapters")
    suspend fun createChapter(
        @Header("Authorization") token: String,
        @Body request: CreateChapterRequest
    ): ChapterDto

    @PATCH("chapters/{id}")
    suspend fun updateChapter(
        @Path("id") chapterId: String,
        @Header("Authorization") token: String,
        @Body request: UpdateChapterRequest
    ): ChapterDto

    @DELETE("chapters/{id}")
    suspend fun deleteChapter(
        @Path("id") chapterId: String,
        @Header("Authorization") token: String
    )
}