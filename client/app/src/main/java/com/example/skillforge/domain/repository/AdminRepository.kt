package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.Course
import com.example.skillforge.domain.model.CourseStructure
import com.example.skillforge.domain.model.User

interface AdminRepository {
    suspend fun getAllUsers(token: String): Result<List<User>>
    suspend fun toggleUserBan(token: String, id: String): Result<User>
    suspend fun createInstructor(token: String, email: String, fullName: String): Result<User>
    suspend fun getCourseQueue(token: String): Result<List<Course>>
    suspend fun getCoursePreview(token: String, id: String): Result<CourseStructure>
    suspend fun moderateCourse(token: String, id: String, status: String, level: String? = null): Result<Course>
}
