package com.example.skillforge.domain.repository

import com.example.skillforge.data.remote.CreateLessonRequest
import com.example.skillforge.data.remote.DiscussionDto
import com.example.skillforge.data.remote.LessonDto
import com.example.skillforge.domain.model.LessonContent

interface LessonRepository {
    suspend fun getLessonDetails(token: String, lessonId: String): Result<LessonContent>
    suspend fun createLesson(token: String, request: CreateLessonRequest): Result<LessonDto>

    suspend fun getDiscussions(token: String, lessonId: String): Result<List<DiscussionDto>>

    suspend fun postDiscussion(
        token: String,
        lessonId: String,
        content: String,
        parentId: String?
    ): Result<DiscussionDto>
}
