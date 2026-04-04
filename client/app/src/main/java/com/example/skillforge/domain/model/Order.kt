package com.example.skillforge.domain.model

data class OrderSummary(
    val id: String,
    val amount: Double,
    val status: String,
    val courseTitle: String,
)
