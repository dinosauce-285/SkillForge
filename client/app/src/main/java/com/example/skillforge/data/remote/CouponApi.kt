package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class CreateCouponRequest(
    val code: String,
    val discountPercent: Int,
    val description: String?,
    val maxUses: Int?,
    val expiresAt: String?,
    val isActive: Boolean
)

data class CouponDto(
    val id: String,
    val code: String,
    val discountPercent: Int,
    val description: String?,
    val maxUses: Int?,
    val usedCount: Int,
    val expiresAt: String?,
    val isActive: Boolean,
    val createdAt: String
)

data class CouponValidationResponse(
    val discountPercent: Int,
    val code: String
)

data class UpdateCouponRequest(
    val code: String? = null,
    val discountPercent: Int? = null,
    val description: String? = null,
    val maxUses: Int? = null,
    val expiresAt: String? = null,
    val isActive: Boolean? = null
)

interface CouponApi {
    @POST("coupons")
    suspend fun createCoupon(
        @Header("Authorization") token: String,
        @Body request: CreateCouponRequest
    ): Response<CouponDto>

    @GET("coupons/instructor")
    suspend fun getInstructorCoupons(
        @Header("Authorization") token: String
    ): Response<List<CouponDto>>

    @GET("coupons/validate/{code}")
    suspend fun validateCoupon(
        @Path("code") code: String,
        @retrofit2.http.Query("courseId") courseId: String
    ): Response<CouponValidationResponse>

    @PATCH("coupons/{id}")
    suspend fun updateCoupon(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: UpdateCouponRequest
    ): Response<CouponDto>

    @PATCH("coupons/{id}/toggle")
    suspend fun toggleCoupon(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<CouponDto>

    @DELETE("coupons/{id}")
    suspend fun deleteCoupon(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>
}
