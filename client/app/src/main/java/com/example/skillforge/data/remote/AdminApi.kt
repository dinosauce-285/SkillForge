package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class CreateInstructorRequest(
    val email: String,
    val fullName: String
)

data class ModerateCourseRequest(
    val status: String,
    val level: String? = null
)

interface AdminApi {
    @GET("admin/users")
    suspend fun getAllUsers(
        @Header("Authorization") token: String
    ): Response<List<AdminUserDto>>

    @PATCH("admin/users/{id}/ban")
    suspend fun toggleUserBan(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<AdminUserDto>

    @POST("admin/users/instructor")
    suspend fun createInstructor(
        @Header("Authorization") token: String,
        @Body request: CreateInstructorRequest
    ): Response<AdminUserDto>

    @GET("admin/courses/queue")
    suspend fun getCourseQueue(
        @Header("Authorization") token: String
    ): Response<List<AdminCourseQueueDto>>

    // Backend returns a flat object with chapters nested inside the course object
    @GET("admin/courses/{id}/preview")
    suspend fun getCoursePreview(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<AdminCoursePreviewDto>

    @PATCH("admin/courses/{id}/moderate")
    suspend fun moderateCourse(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: ModerateCourseRequest
    ): Response<AdminCourseQueueDto>

    @GET("admin/coupons/platform")
    suspend fun getPlatformCoupons(
        @Header("Authorization") token: String
    ): Response<List<AdminPlatformCouponDto>>

    @POST("admin/coupons/platform")
    suspend fun createPlatformCoupon(
        @Header("Authorization") token: String,
        @Body request: CreatePlatformCouponRequest
    ): Response<AdminPlatformCouponDto>

    @PATCH("admin/coupons/platform/{id}")
    suspend fun updatePlatformCoupon(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: UpdatePlatformCouponRequest
    ): Response<AdminPlatformCouponDto>

    @DELETE("admin/coupons/platform/{id}")
    suspend fun deactivatePlatformCoupon(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<AdminPlatformCouponDto>

    @GET("admin/finance/summary")
    suspend fun getFinanceSummary(
        @Header("Authorization") token: String,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<AdminFinanceSummaryDto>

    @GET("admin/finance/snapshots")
    suspend fun getFinanceSnapshots(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<AdminFinanceSnapshotListDto>
}
