package com.example.skillforge.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateInstructorSubscriptionRequestDto(
    @SerializedName("mockPaymentConfirmed")
    val mockPaymentConfirmed: Boolean,
    @SerializedName("planCode")
    val planCode: String? = null,
)

data class InstructorSubscriptionResponseDto(
    @SerializedName("message")
    val message: String,
    @SerializedName("subscription")
    val subscription: InstructorSubscriptionDto,
    @SerializedName("user")
    val user: InstructorSubscriptionUserDto,
)

data class InstructorSubscriptionDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("planCode")
    val planCode: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("paymentStatus")
    val paymentStatus: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("mockPaymentReference")
    val mockPaymentReference: String,
    @SerializedName("startedAt")
    val startedAt: String,
    @SerializedName("expiresAt")
    val expiresAt: String?,
)

data class InstructorSubscriptionUserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("role")
    val role: String,
)
