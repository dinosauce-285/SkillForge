package com.example.skillforge.feature.transaction.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.domain.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TransactionUiState(
    val courseTitle: String = "UI/UX Design Basic Course",
    val instructorName: String = "Nguyen Van A",
    val basePrice: Long = 1500000,
    val discountAmount: Long = 200000,
    val promoCode: String = "",
    val discountRate: Double = 0.0,
    val successMessage: String? = null,
    val errorMessage: String? = null,
) {
    val basePrice: Double get() = course?.price ?: 0.0
    val discountAmount: Double get() = basePrice * discountRate
    val totalPrice: Double get() = (basePrice - discountAmount).coerceAtLeast(0.0)
}

class TransactionViewModel(
    private val courseRepository: CourseRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionUiState(isLoading = true))
    val uiState: StateFlow<TransactionUiState> = _uiState

    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)

            courseRepository.getCourseDetails(courseId).fold(
                onSuccess = { course ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        course = course,
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load checkout data",
                    )
                },
            )
        }
    }

    fun onPromoCodeChange(code: String) {
        _uiState.value = _uiState.value.copy(promoCode = code)
    }

    fun applyPromoCode() {
        val code = _uiState.value.promoCode.trim()
        val discountRate = if (code.equals("CSC13009", ignoreCase = true)) 0.5 else 0.0
        _uiState.value = _uiState.value.copy(discountRate = discountRate)
    }

    fun confirmPayment(token: String, courseId: String) {
        val totalPrice = _uiState.value.totalPrice

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null, successMessage = null)

            orderRepository.createOrder(token, courseId, totalPrice).fold(
                onSuccess = { order ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        successMessage = "Order ${order.id.take(8)} created successfully",
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        errorMessage = error.message ?: "Unable to create order",
                    )
                },
            )
        }
    }
}

class TransactionViewModelFactory(
    private val courseRepository: CourseRepository,
    private val orderRepository: OrderRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(courseRepository, orderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
