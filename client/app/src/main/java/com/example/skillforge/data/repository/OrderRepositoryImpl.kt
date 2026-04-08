package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.CreateOrderRequest
import com.example.skillforge.data.remote.OrderApi
import com.example.skillforge.domain.model.OrderSummary
import com.example.skillforge.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val api: OrderApi,
) : OrderRepository {

    override suspend fun createOrder(token: String, courseId: String, amount: Double): Result<OrderSummary> {
        return try {
            val response = api.createOrder(
                token = bearer(token),
                request = CreateOrderRequest(courseId = courseId, amount = amount),
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
                Result.failure(Exception("Failed to create order"))
            }
        } catch (e: Exception) {
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
}
