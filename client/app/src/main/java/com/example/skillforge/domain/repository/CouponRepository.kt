package com.example.skillforge.domain.repository

import com.example.skillforge.data.remote.CouponDto
import com.example.skillforge.data.remote.CouponValidationResponse
import com.example.skillforge.data.remote.UpdateCouponRequest

interface CouponRepository {
    suspend fun createCoupon(
        code: String,
        discountPercent: Int,
        description: String?,
        maxUses: Int?,
        expiresAt: String?,
        isActive: Boolean
    ): Result<CouponDto>
    suspend fun getInstructorCoupons(): Result<List<CouponDto>>
    suspend fun validateCoupon(code: String, courseId: String): Result<CouponValidationResponse>
    suspend fun updateCoupon(id: String, request: UpdateCouponRequest): Result<CouponDto>
    suspend fun toggleCoupon(id: String): Result<CouponDto>
    suspend fun deleteCoupon(id: String): Result<Unit>
}
