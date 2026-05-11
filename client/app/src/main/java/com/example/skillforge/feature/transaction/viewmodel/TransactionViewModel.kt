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
    val courseDiscounts: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val promoMessage: String? = null,   // success msg for promo
    val promoApplied: Boolean = false,  // true = applied successfully
    val orderSuccessful: Boolean = false,
) {
    val discountAmount: Double get() = courses.sumOf { 
         val discount = courseDiscounts[it.id] ?: 0
         it.price * (discount / 100.0) 
    }
    val totalPrice: Double get() = courses.sumOf { it.price }  - discountAmount
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
        _uiState.update { it.copy(promoCode = code, promoMessage = null, promoApplied = false, courseDiscounts = emptyMap()) }
    }

    fun applyPromoCode() {
        val code = _uiState.value.promoCode.trim()
        if (code.isEmpty() || _uiState.value.courses.isEmpty()) {
            _uiState.update { it.copy(courseDiscounts = emptyMap(), promoMessage = null, promoApplied = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, promoMessage = null, promoApplied = false) }
            val newDiscounts = mutableMapOf<String, Int>()
            var anyApplied = false
            var appliedDiscountPercent = 0

            for (course in _uiState.value.courses) {
                val result = couponRepository.validateCoupon(code, course.id)
                if (result.isSuccess) {
                    val validationResponse = result.getOrNull()
                    if (validationResponse != null) {
                        newDiscounts[course.id] = validationResponse.discountPercent
                        appliedDiscountPercent = validationResponse.discountPercent
                        anyApplied = true
                    }
                } else {
                    newDiscounts[course.id] = 0
                }
            }

            if (anyApplied) {
                _uiState.update {
                    it.copy(
                        courseDiscounts = newDiscounts,
                        promoMessage = "Code applied! ${appliedDiscountPercent}% off applicable courses",
                        promoApplied = true,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        courseDiscounts = emptyMap(),
                        promoMessage = "Invalid promo code or not applicable",
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
            val promoCode = _uiState.value.promoCode
            
            var allSuccessful = true
            for (course in courses) {
                val discount = _uiState.value.courseDiscounts[course.id] ?: 0
                val priceWithDiscount = course.price * (1 - (discount / 100.0))
                val couponCode = if (discount > 0) promoCode else null
                
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
