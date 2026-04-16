package com.example.skillforge.feature.student_courses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ReviewState {
    object Idle : ReviewState()
    object Loading : ReviewState()
    object Success : ReviewState()
    data class Error(val message: String) : ReviewState()
}

class ReviewViewModel(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReviewState>(ReviewState.Idle)
    val uiState: StateFlow<ReviewState> = _uiState

    fun submitReview(token: String, courseId: String, rating: Int, content: String) {
        viewModelScope.launch {
            _uiState.value = ReviewState.Loading

            reviewRepository.submitReview(token, courseId, rating, content)
                .onSuccess {
                    _uiState.value = ReviewState.Success
                }
                .onFailure { error ->
                    _uiState.value = ReviewState.Error(error.message ?: "Failed to submit review")
                }
        }
    }

    fun resetState() {
        _uiState.value = ReviewState.Idle
    }
}