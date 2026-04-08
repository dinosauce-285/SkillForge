package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import com.google.gson.annotations.SerializedName

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
}

data class PostDiscussionRequest(
    val content: String,
    val parentId: String? = null
)