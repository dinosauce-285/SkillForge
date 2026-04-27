package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class CreateOrderRequest(
    val courseId: String,
    val amount: Double,
    val couponCode: String? = null,
)

data class OrderCourseDto(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
)

data class OrderDto(
    val id: String,
    val amount: Double,
    val status: String,
    val course: OrderCourseDto,
)

interface OrderApi {
    @POST("orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest,
    ): Response<OrderDto>

    @GET("orders")
    suspend fun getOrders(
        @Header("Authorization") token: String,
    ): Response<List<OrderDto>>
}
