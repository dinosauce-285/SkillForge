package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.OrderSummary

interface OrderRepository {
    suspend fun createOrder(token: String, courseId: String, amount: Double): Result<OrderSummary>
    suspend fun getOrders(token: String): Result<List<OrderSummary>>
}
