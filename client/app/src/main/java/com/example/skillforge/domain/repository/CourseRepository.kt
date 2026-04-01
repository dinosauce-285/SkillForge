package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.Category
import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.domain.model.CourseSummary

interface CourseRepository {
    suspend fun getCourses(
        searchQuery: String? = null,
        categoryId: String? = null
    ): Result<List<CourseSummary>>

    suspend fun getCategories(): Result<List<Category>>

    suspend fun getCourseDetails(courseId: String): Result<CourseDetails>

    suspend fun createCourse(
        token: String,
        title: String,
        summary: String,
        price: Double,
        categoryId: String
    ): Result<Unit>
}