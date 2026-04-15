package com.example.skillforge.domain.repository

import com.example.skillforge.data.remote.CourseManagerDto
import com.example.skillforge.data.remote.CourseSummaryDto
import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.domain.model.CourseSummary
import java.io.File

interface CourseRepository {
    suspend fun getCourses(
        searchQuery: String? = null,
        categoryId: String? = null,
        level: String? = null,
    ): Result<List<CourseSummary>>

    suspend fun getCourseDetails(courseId: String): Result<CourseDetails>

    suspend fun createCourse(
        token: String,
        title: String,
        summary: String,
        price: Double,
        categoryId: String,
        status: String,
        thumbnailFile: File?
    ): Result<CourseSummaryDto>

    suspend fun getCourseForManager(token: String, courseId: String): Result<CourseManagerDto>
    suspend fun getMyCourses(token: String): Result<List<CourseSummaryDto>>
    suspend fun getEnrollmentStatus(token: String, courseId: String): Result<Boolean>
}
