package com.example.skillforge.data.remote

import com.example.skillforge.data.remote.dto.CreateInstructorSubscriptionRequestDto
import com.example.skillforge.data.remote.dto.InstructorSubscriptionResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SubscriptionApi {
    @POST("subscriptions/instructor")
    suspend fun createInstructorSubscription(
        @Body request: CreateInstructorSubscriptionRequestDto,
    ): Response<InstructorSubscriptionResponseDto>
}
