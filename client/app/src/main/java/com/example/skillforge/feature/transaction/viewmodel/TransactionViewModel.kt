package com.example.skillforge.feature.transaction.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TransactionUiState(
    val courseTitle: String = "UI/UX Design Basic Course",
    val instructorName: String = "Nguyen Van A",
    val basePrice: Long = 1500000,
    val discountAmount: Long = 200000,
    val promoCode: String = "",
    val isLoading: Boolean = false
) {
    val totalPrice: Long get() = basePrice - discountAmount
}

class TransactionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    fun onPromoCodeChange(code: String) {
        _uiState.value = _uiState.value.copy(promoCode = code)
    }

    fun applyPromoCode() {
        // Logic to apply promo code
    }

    fun confirmPayment() {
        // Logic to initiate payment
    }
}
