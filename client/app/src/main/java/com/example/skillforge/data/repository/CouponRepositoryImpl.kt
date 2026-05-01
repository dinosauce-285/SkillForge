package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.CouponApi
import com.example.skillforge.data.remote.CouponDto
import com.example.skillforge.data.remote.CouponValidationResponse
import com.example.skillforge.data.remote.CreateCouponRequest
import com.example.skillforge.domain.repository.CouponRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class CouponRepositoryImpl(
    private val api: CouponApi
) : CouponRepository {
    override suspend fun createCoupon(
        code: String,
        discountPercent: Int,
        isActive: Boolean
    ): Result<CouponDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.createCoupon(
                token = "", // Token is injected via interceptor
                request = CreateCouponRequest(code, discountPercent, isActive)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = try {
                    errorBody?.let { JSONObject(it).getString("message") }
                } catch (e: Exception) { null } ?: "Failed to create coupon"
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInstructorCoupons(): Result<List<CouponDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getInstructorCoupons(token = "") // Token injected
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch coupons"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validateCoupon(code: String, courseId: String): Result<CouponValidationResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.validateCoupon(code, courseId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = try {
                    errorBody?.let { JSONObject(it).getString("message") }
                } catch (e: Exception) { null } ?: "Invalid coupon"
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCoupon(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteCoupon(token = "", id = id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete coupon"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
