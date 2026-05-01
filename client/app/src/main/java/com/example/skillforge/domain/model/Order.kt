package com.example.skillforge.domain.model

data class OrderSummary(
    val id: String,
    val amount: Double,
    val status: String,
    val courseTitle: String,
    val courseId: String,
    val originalPrice: Double,
    val discountPercent: Int? = null,
    val createdAt: String? = null,
    val courseThumbnailUrl: String? = null,
)
