package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.domain.model.CourseSummary

interface CourseRepository {
    suspend fun getCourses(): Result<List<CourseSummary>>

    suspend fun getCourseDetails(courseId: String): Result<CourseDetails>
}
