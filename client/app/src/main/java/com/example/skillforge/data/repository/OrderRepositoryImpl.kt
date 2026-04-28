package com.example.skillforge.data.repository

import android.util.Log
import com.example.skillforge.data.remote.CreateOrderRequest
import com.example.skillforge.data.remote.OrderApi
import com.example.skillforge.domain.model.OrderSummary
import com.example.skillforge.domain.repository.OrderRepository
import org.json.JSONArray
import org.json.JSONObject

private const val ORDER_REPOSITORY_TAG = "OrderRepository"

class OrderRepositoryImpl(
    private val api: OrderApi,
) : OrderRepository {

    override suspend fun createOrder(token: String, courseId: String, amount: Double, couponCode: String?): Result<OrderSummary> {
        return try {
            val request = CreateOrderRequest(courseId = courseId, amount = amount, couponCode = couponCode)
            Log.d(
                ORDER_REPOSITORY_TAG,
                "POST /orders request: courseId=${request.courseId}, amount=${request.amount}, couponCode=${request.couponCode}"
            )

            val response = api.createOrder(
                token = bearer(token),
                request = request,
            )

            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(
                    OrderSummary(
                        id = dto.id,
                        amount = dto.amount,
                        status = dto.status,
                        courseTitle = dto.course.title,
                        courseId = dto.course.id,
                    ),
                )
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(
                    ORDER_REPOSITORY_TAG,
                    "POST /orders failed. status=${response.code()}, body=$errorBody"
                )
                Result.failure(Exception(extractErrorMessage(errorBody) ?: "Failed to create order"))
            }
        } catch (e: Exception) {
            Log.e(ORDER_REPOSITORY_TAG, "POST /orders failed", e)
            Result.failure(Exception(e.message ?: "Failed to create order"))
        }
    }

    override suspend fun getOrders(token: String): Result<List<OrderSummary>> {
        return try {
            val response = api.getOrders(bearer(token))

            if (response.isSuccessful && response.body() != null) {
                Result.success(
                    response.body()!!.map { dto ->
                        OrderSummary(
                            id = dto.id,
                            amount = dto.amount,
                            status = dto.status,
                            courseTitle = dto.course.title,
                            courseId = dto.course.id,
                        )
                    },
                )
            } else {
                Result.failure(Exception("Failed to load orders"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to load orders"))
        }
    }

    private fun bearer(token: String): String {
        return if (token.startsWith("Bearer ")) token else "Bearer $token"
    }

    private fun extractErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null

        return try {
            val json = JSONObject(errorBody)
            when (val message = json.opt("message")) {
                is JSONArray -> {
                    (0 until message.length())
                        .mapNotNull { index -> message.optString(index).takeIf { it.isNotBlank() } }
                        .joinToString(separator = "\n")
                        .takeIf { it.isNotBlank() }
                }
                is String -> message.takeIf { it.isNotBlank() }
                else -> json.optString("error").takeIf { it.isNotBlank() }
            }
        } catch (e: Exception) {
            errorBody.takeIf { it.isNotBlank() }
        }
    }
}
