package com.example.skillforge.domain.repository

import com.example.skillforge.data.remote.CouponDto
import com.example.skillforge.data.remote.CouponValidationResponse

interface CouponRepository {
    suspend fun createCoupon(code: String, discountPercent: Int, isActive: Boolean): Result<CouponDto>
    suspend fun getInstructorCoupons(): Result<List<CouponDto>>
    suspend fun validateCoupon(code: String, courseId: String): Result<CouponValidationResponse>
    suspend fun deleteCoupon(id: String): Result<Unit>
}
