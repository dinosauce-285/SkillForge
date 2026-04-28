package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class CreateCouponRequest(
    val code: String,
    val discountPercent: Int,
    val isActive: Boolean
)

data class CouponDto(
    val id: String,
    val code: String,
    val discountPercent: Int,
    val isActive: Boolean,
    val createdAt: String
)

data class CouponValidationResponse(
    val discountPercent: Int,
    val code: String
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
        @Path("code") code: String
    ): Response<CouponValidationResponse>

    @DELETE("coupons/{id}")
    suspend fun deleteCoupon(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>
}
