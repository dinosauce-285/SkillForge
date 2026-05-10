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
    val courses: List<CourseSummary> = emptyList(),
    val promoCode: String = "",
    val discountPercent: Int = 0,
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val promoMessage: String? = null,   // success msg for promo
    val promoApplied: Boolean = false,  // true = applied successfully
    val orderSuccessful: Boolean = false,
) {
    val totalPrice: Double get() = courses.sumOf { it.price } * (1 - (discountPercent / 100.0))
    val course: CourseSummary? get() = courses.firstOrNull() // For backward compatibility in UI if needed
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

    fun loadCourses(courseIds: String) {
        viewModelScope.launch {
            _uiState.value = TransactionUiState(isLoading = true)
            val ids = courseIds.split(",").filter { it.isNotBlank() }
            val loadedCourses = mutableListOf<CourseSummary>()
            
            var hasError = false
            for (id in ids) {
                courseRepository.getCourseDetails(id).fold(
                    onSuccess = { courseDetails ->
                        loadedCourses.add(
                            CourseSummary(
                                id = courseDetails.id,
                                title = courseDetails.title,
                                subtitle = courseDetails.subtitle,
                                summary = courseDetails.summary,
                                thumbnailUrl = courseDetails.thumbnailUrl,
                                categoryId = "",
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
                        )
                    },
                    onFailure = { error ->
                        hasError = true
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to load course $id"
                            )
                        }
                    }
                )
                if (hasError) break
            }

            if (!hasError) {
                _uiState.update { it.copy(courses = loadedCourses, isLoading = false) }
            }
        }
    }

    fun onPromoCodeChange(code: String) {
        _uiState.update { it.copy(promoCode = code, promoMessage = null, promoApplied = false, discountPercent = 0) }
    }

    fun applyPromoCode() {
        val code = _uiState.value.promoCode.trim()
        val firstCourseId = _uiState.value.courses.firstOrNull()?.id
        if (code.isEmpty() || firstCourseId == null) {
            _uiState.update { it.copy(discountPercent = 0, promoMessage = null, promoApplied = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, promoMessage = null, promoApplied = false) }
            // For now, apply coupon to the first course or handle it as a general discount if supported
            val result = couponRepository.validateCoupon(code, firstCourseId)
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
        val courses = _uiState.value.courses
        if (courses.isEmpty()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            val couponCode = if (_uiState.value.discountPercent > 0) _uiState.value.promoCode else null
            
            var allSuccessful = true
            for (course in courses) {
                val priceWithDiscount = course.price * (1 - (_uiState.value.discountPercent / 100.0))
                val result = orderRepository.createOrder(token, course.id, priceWithDiscount, couponCode)
                if (result.isFailure) {
                    allSuccessful = false
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            errorMessage = "Failed to purchase ${course.title}: ${result.exceptionOrNull()?.message}"
                        )
                    }
                    break
                }
            }

            if (allSuccessful) {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        orderSuccessful = true
                    )
                }
            }
        }
    }
}
