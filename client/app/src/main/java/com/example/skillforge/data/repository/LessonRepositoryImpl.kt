package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.CreateLessonRequest
import com.example.skillforge.data.remote.DiscussionApi
import com.example.skillforge.data.remote.DiscussionDto
import com.example.skillforge.data.remote.LessonApi
import com.example.skillforge.domain.repository.LessonRepository
import com.example.skillforge.data.remote.LessonDto
import com.example.skillforge.data.remote.PostDiscussionRequest
import com.example.skillforge.domain.model.LessonContent
import com.example.skillforge.domain.model.LessonMaterial

class LessonRepositoryImpl(private val api: LessonApi,
                           private val discussionApi: DiscussionApi
) : LessonRepository {
    override suspend fun getLessonDetails(token: String, lessonId: String): Result<LessonContent> {
        return try {
            val response = api.getLessonDetails(lessonId, "Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(
                    LessonContent(
                        id = dto.id,
                        title = dto.title,
                        chapterTitle = dto.chapter.title,
                        courseTitle = dto.chapter.course.title,
                        materials = dto.materials.map { material ->
                            LessonMaterial(
                                id = material.id,
                                type = material.type.name,
                                fileUrl = material.fileUrl,
                                fileSize = material.fileSize,
                                status = material.status.name,
                            )
                        },
                    ),
                )
            } else {
                android.util.Log.e("SKILLFORGE_DEBUG", "Failed to load lesson. Code: ${response.code()}, ErrorBody: ${response.errorBody()?.string()}")
                Result.failure(Exception("Failed to load lesson"))
            }
        } catch (e: Exception) {
            android.util.Log.e("SKILLFORGE_DEBUG", "Exception when fetching lesson: ", e)
            Result.failure(Exception(e.message ?: "Failed to load lesson"))
        }
    }

    override suspend fun createLesson(token: String, request: CreateLessonRequest): Result<LessonDto> {
        return try {
            val response = api.createLesson("Bearer $token", request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDiscussions(token: String, lessonId: String): Result<List<DiscussionDto>> {
        return try {
            // Use discussionApi here
            val response = discussionApi.getDiscussions("Bearer $token", lessonId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch discussions. Code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun postDiscussion(
        token: String,
        lessonId: String,
        content: String,
        parentId: String?
    ): Result<DiscussionDto> {
        return try {
            val request = PostDiscussionRequest(content, parentId)
            // Use discussionApi here
            val response = discussionApi.postDiscussion("Bearer $token", lessonId, request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to post discussion. Code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInstructorDiscussions(
        token: String,
        courseId: String?,
        unansweredOnly: Boolean
    ): Result<List<com.example.skillforge.data.remote.InstructorDiscussionDto>> {
        return try {
            val response = discussionApi.getInstructorDiscussions(
                token = "Bearer $token",
                courseId = courseId,
                unansweredOnly = unansweredOnly
            )
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch instructor discussions. Code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun replyToDiscussion(
        token: String,
        discussionId: String,
        lessonId: String,
        content: String
    ): Result<com.example.skillforge.data.remote.InstructorDiscussionDto> {
        return try {
            val request = com.example.skillforge.data.remote.ReplyDiscussionRequest(content, lessonId)
            val response = discussionApi.replyToDiscussion("Bearer $token", discussionId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to reply to discussion. Code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
