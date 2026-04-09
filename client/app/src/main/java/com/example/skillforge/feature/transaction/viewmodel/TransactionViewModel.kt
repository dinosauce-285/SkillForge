package com.example.skillforge.feature.transaction.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.CourseSummary
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.domain.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionUiState(
    val course: CourseSummary? = null,
    val promoCode: String = "",
    val discountAmount: Long = 0,
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val orderSuccessful: Boolean = false,
) {
    val totalPrice: Long get() = (course?.price?.toLong() ?: 0L) - discountAmount
}

class TransactionViewModel(
    private val courseRepository: CourseRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            courseRepository.getCourseDetails(courseId).fold(
                onSuccess = { courseDetails ->
                    val summary = CourseSummary(
                        id = courseDetails.id,
                        title = courseDetails.title,
                        subtitle = courseDetails.subtitle,
                        summary = courseDetails.summary,
                        thumbnailUrl = courseDetails.thumbnailUrl,
                        categoryId = "", // Not needed for transaction
                        categoryName = courseDetails.categoryName,
                        instructorName = courseDetails.instructorName,
                        level = courseDetails.level,
                        price = courseDetails.price,
                        isFree = courseDetails.isFree,
                        averageRating = courseDetails.averageRating,
                        studentCount = courseDetails.studentCount,
                        reviewCount = courseDetails.reviewCount,
                        chapterCount = courseDetails.chapterCount,
                        tags = courseDetails.tags,
                    )
                    _uiState.update { it.copy(course = summary, isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load course"
                        )
                    }
                }
            )
        }
    }

    fun onPromoCodeChange(code: String) {
        _uiState.update { it.copy(promoCode = code) }
    }

    fun applyPromoCode() {
        // Logic to apply promo code (simplified for now)
        if (_uiState.value.promoCode.isEmpty()) {
            _uiState.update { it.copy(discountAmount = 0) }
        } else {
            // Simple discount logic: 10% off
            val discount = ((_uiState.value.course?.price ?: 0.0) * 0.1).toLong()
            _uiState.update { it.copy(discountAmount = discount) }
        }
    }

    fun confirmPayment(token: String) {
        val course = _uiState.value.course ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            orderRepository.createOrder(token, course.id, course.price - (_uiState.value.discountAmount / 100.0)).fold(
                onSuccess = { order ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            orderSuccessful = true
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            errorMessage = error.message ?: "Failed to create order"
                        )
                    }
                }
            )
        }
    }
}
