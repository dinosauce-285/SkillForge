package com.example.skillforge.domain.model

data class InstructorSubscription(
    val id: String,
    val planCode: String,
    val status: String,
    val paymentStatus: String,
    val amount: String,
    val currency: String,
    val mockPaymentReference: String,
    val startedAt: String,
    val expiresAt: String?,
)

data class InstructorSubscriptionUser(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
)

data class InstructorSubscriptionActivation(
    val message: String,
    val subscription: InstructorSubscription,
    val user: InstructorSubscriptionUser,
)
