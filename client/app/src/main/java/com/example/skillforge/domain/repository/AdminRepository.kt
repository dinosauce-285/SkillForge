package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.Course
import com.example.skillforge.domain.model.CourseStructure
import com.example.skillforge.domain.model.User
import com.example.skillforge.data.remote.AdminFinanceSnapshotListDto
import com.example.skillforge.data.remote.AdminFinanceSummaryDto
import com.example.skillforge.data.remote.AdminPlatformCouponDto

interface AdminRepository {
    suspend fun getAllUsers(token: String): Result<List<User>>
    suspend fun toggleUserBan(token: String, id: String): Result<User>
    suspend fun createInstructor(token: String, email: String, fullName: String): Result<User>
    suspend fun getCourseQueue(token: String): Result<List<Course>>
    suspend fun getCoursePreview(token: String, id: String): Result<CourseStructure>
    suspend fun moderateCourse(token: String, id: String, status: String, level: String? = null): Result<Course>
    suspend fun getPlatformCoupons(token: String): Result<List<AdminPlatformCouponDto>>
    suspend fun createPlatformCoupon(
        token: String,
        code: String,
        discountPercent: Int,
        isActive: Boolean
    ): Result<AdminPlatformCouponDto>
    suspend fun updatePlatformCoupon(
        token: String,
        id: String,
        code: String? = null,
        discountPercent: Int? = null,
        isActive: Boolean? = null
    ): Result<AdminPlatformCouponDto>
    suspend fun deactivatePlatformCoupon(token: String, id: String): Result<AdminPlatformCouponDto>
    suspend fun getFinanceSummary(
        token: String,
        startDate: String? = null,
        endDate: String? = null
    ): Result<AdminFinanceSummaryDto>
    suspend fun getFinanceSnapshots(
        token: String,
        page: Int = 1,
        limit: Int = 20,
        startDate: String? = null,
        endDate: String? = null
    ): Result<AdminFinanceSnapshotListDto>
}
