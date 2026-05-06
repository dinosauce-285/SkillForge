package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.AdminApi
import com.example.skillforge.data.remote.AdminCoursePreviewDto
import com.example.skillforge.data.remote.AdminCourseQueueDto
import com.example.skillforge.data.remote.AdminFinanceSnapshotListDto
import com.example.skillforge.data.remote.AdminFinanceSummaryDto
import com.example.skillforge.data.remote.AdminPlatformCouponDto
import com.example.skillforge.data.remote.AdminUserDto
import com.example.skillforge.data.remote.CreatePlatformCouponRequest
import com.example.skillforge.data.remote.CreateInstructorRequest
import com.example.skillforge.data.remote.ModerateCourseRequest
import com.example.skillforge.data.remote.UpdatePlatformCouponRequest
import com.example.skillforge.domain.model.Category
import com.example.skillforge.domain.model.Course
import com.example.skillforge.domain.model.CourseChapter
import com.example.skillforge.domain.model.CourseLesson
import com.example.skillforge.domain.model.CourseQuiz
import com.example.skillforge.domain.model.CourseStructure
import com.example.skillforge.domain.model.User
import com.example.skillforge.domain.model.UserProfile
import com.example.skillforge.domain.repository.AdminRepository

class AdminRepositoryImpl(
    private val api: AdminApi
) : AdminRepository {

    private fun AdminUserDto.toDomain(): User = User(
        id = this.id,
        email = this.email,
        fullName = this.fullName,
        role = this.role,
        isActive = this.isActive,
        profile = this.profile?.let { p ->
            UserProfile(
                avatarUrl = p.avatarUrl,
                skills = p.skills ?: emptyList(),
                learningGoals = p.learningGoals
            )
        }
    )

    private fun AdminCourseQueueDto.toDomain(): Course = Course(
        id = this.id,
        title = this.title,
        level = this.level ?: "BEGINNER",
        status = this.status ?: "PENDING",
        instructor = this.instructor?.let {
            User(id = it.id, email = it.email, fullName = it.fullName, role = "INSTRUCTOR", isActive = true, profile = null)
        },
        category = this.category?.let { Category(id = it.id, name = it.name) }
    )

    private fun AdminCoursePreviewDto.toDomainStructure(): CourseStructure {
        val course = Course(
            id = this.id,
            title = this.title,
            level = this.level ?: "BEGINNER",
            status = this.status ?: "PENDING",
            instructor = this.instructor?.let {
                User(id = it.id, email = it.email, fullName = it.fullName, role = "INSTRUCTOR", isActive = true, profile = null)
            },
            category = this.category?.let { Category(id = it.id, name = it.name) }
        )
        val chapters = this.chapters?.map { ch ->
            CourseChapter(
                id = ch.id,
                title = ch.title,
                lessons = ch.lessons?.map { l -> CourseLesson(id = l.id, title = l.title) } ?: emptyList(),
                quizzes = ch.quizzes?.map { q -> CourseQuiz(id = q.id, title = q.title) } ?: emptyList()
            )
        }
        return CourseStructure(course = course, chapters = chapters)
    }

    override suspend fun getAllUsers(token: String): Result<List<User>> {
        return try {
            val response = api.getAllUsers("Bearer $token")
            if (response.isSuccessful) {
                Result.success((response.body() ?: emptyList()).map { it.toDomain() })
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleUserBan(token: String, id: String): Result<User> {
        return try {
            val response = api.toggleUserBan("Bearer $token", id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createInstructor(token: String, email: String, fullName: String): Result<User> {
        return try {
            val response = api.createInstructor("Bearer $token", CreateInstructorRequest(email, fullName))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCourseQueue(token: String): Result<List<Course>> {
        return try {
            val response = api.getCourseQueue("Bearer $token")
            if (response.isSuccessful) {
                Result.success((response.body() ?: emptyList()).map { it.toDomain() })
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCoursePreview(token: String, id: String): Result<CourseStructure> {
        return try {
            val response = api.getCoursePreview("Bearer $token", id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomainStructure())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun moderateCourse(token: String, id: String, status: String, level: String?): Result<Course> {
        return try {
            val response = api.moderateCourse("Bearer $token", id, ModerateCourseRequest(status, level))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlatformCoupons(token: String): Result<List<AdminPlatformCouponDto>> {
        return try {
            val response = api.getPlatformCoupons("Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPlatformCoupon(
        token: String,
        code: String,
        discountPercent: Int,
        isActive: Boolean
    ): Result<AdminPlatformCouponDto> {
        return try {
            val response = api.createPlatformCoupon(
                "Bearer $token",
                CreatePlatformCouponRequest(code, discountPercent, isActive)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlatformCoupon(
        token: String,
        id: String,
        code: String?,
        discountPercent: Int?,
        isActive: Boolean?
    ): Result<AdminPlatformCouponDto> {
        return try {
            val response = api.updatePlatformCoupon(
                "Bearer $token",
                id,
                UpdatePlatformCouponRequest(code, discountPercent, isActive)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deactivatePlatformCoupon(
        token: String,
        id: String
    ): Result<AdminPlatformCouponDto> {
        return try {
            val response = api.deactivatePlatformCoupon("Bearer $token", id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFinanceSummary(
        token: String,
        startDate: String?,
        endDate: String?
    ): Result<AdminFinanceSummaryDto> {
        return try {
            val response = api.getFinanceSummary("Bearer $token", startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFinanceSnapshots(
        token: String,
        page: Int,
        limit: Int,
        startDate: String?,
        endDate: String?
    ): Result<AdminFinanceSnapshotListDto> {
        return try {
            val response = api.getFinanceSnapshots(
                token = "Bearer $token",
                page = page,
                limit = limit,
                startDate = startDate,
                endDate = endDate
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
