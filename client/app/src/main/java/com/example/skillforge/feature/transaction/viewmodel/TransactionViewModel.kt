package com.example.skillforge.feature.transaction.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.CourseSummary
import com.example.skillforge.domain.repository.CouponRepository
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
    val discountPercent: Int = 0,
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val promoMessage: String? = null,   // success msg for promo
    val promoApplied: Boolean = false,  // true = applied successfully
    val orderSuccessful: Boolean = false,
) {
    val totalPrice: Double get() = (course?.price ?: 0.0) * (1 - (discountPercent / 100.0))
}

class TransactionViewModel(
    private val courseRepository: CourseRepository,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.value = TransactionUiState()
    }

    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            _uiState.value = TransactionUiState(isLoading = true)
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
        _uiState.update { it.copy(promoCode = code, promoMessage = null, promoApplied = false, discountPercent = 0) }
    }

    fun applyPromoCode() {
        val code = _uiState.value.promoCode.trim()
        val courseId = _uiState.value.course?.id
        if (code.isEmpty() || courseId == null) {
            _uiState.update { it.copy(discountPercent = 0, promoMessage = null, promoApplied = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, promoMessage = null, promoApplied = false) }
            val result = couponRepository.validateCoupon(code, courseId)
            if (result.isSuccess) {
                val validationResponse = result.getOrNull()
                if (validationResponse != null) {
                    _uiState.update {
                        it.copy(
                            discountPercent = validationResponse.discountPercent,
                            promoMessage = "Code applied! ${validationResponse.discountPercent}% off",
                            promoApplied = true,
                            isLoading = false
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        discountPercent = 0,
                        promoMessage = result.exceptionOrNull()?.message ?: "Invalid promo code",
                        promoApplied = false,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun confirmPayment(token: String) {
        val course = _uiState.value.course ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            val couponCode = if (_uiState.value.discountPercent > 0) _uiState.value.promoCode else null
            orderRepository.createOrder(token, course.id, _uiState.value.totalPrice, couponCode).fold(
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
