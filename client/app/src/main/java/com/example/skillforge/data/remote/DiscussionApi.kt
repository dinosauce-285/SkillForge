package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import com.google.gson.annotations.SerializedName
import retrofit2.http.Query

data class DiscussionDto(
    val id: String,
    val lessonId: String,
    val userId: String,
    val parentId: String?,
    val content: String,
    val createdAt: String,
    val user: UserShortDto,
    val replies: List<DiscussionDto>? = emptyList()
)

data class UserShortDto(
    val id: String,
    val fullName: String,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

interface DiscussionApi {
    @GET("lessons/{lessonId}/discussions")
    suspend fun getDiscussions(
        @Header("Authorization") token: String,
        @Path("lessonId") lessonId: String
    ): Response<List<DiscussionDto>>

    @POST("lessons/{lessonId}/discussions")
    suspend fun postDiscussion(
        @Header("Authorization") token: String,
        @Path("lessonId") lessonId: String,
        @Body request: PostDiscussionRequest
    ): Response<DiscussionDto>

    @GET("instructor/discussions")
    suspend fun getInstructorDiscussions(
        @Header("Authorization") token: String,
        @Query("courseId") courseId: String? = null,
        @Query("unansweredOnly") unansweredOnly: Boolean = true
    ): Response<List<InstructorDiscussionDto>>

    @POST("instructor/discussions/{id}/reply")
    suspend fun replyToDiscussion(
        @Header("Authorization") token: String,
        @Path("id") discussionId: String,
        @Body request: ReplyDiscussionRequest
    ): Response<InstructorDiscussionDto>
}

data class PostDiscussionRequest(
    val content: String,
    val parentId: String? = null
)

data class InstructorDiscussionDto(
    val id: String,
    val lessonId: String,
    val userId: String,
    val parentId: String?,
    val content: String,
    val createdAt: String,
    val lesson: DiscussionLessonDto,
    val user: UserShortDto,
    val replies: List<InstructorDiscussionDto>?
)

data class DiscussionLessonDto(
    val title: String,
    val course: DiscussionCourseDto
)

data class DiscussionCourseDto(
    val id: String,
    val title: String
)

data class ReplyDiscussionRequest(
    val content: String,
    val lessonId: String
)