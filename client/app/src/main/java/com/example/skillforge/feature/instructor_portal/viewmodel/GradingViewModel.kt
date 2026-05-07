package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.dto.QuizSubmissionDetailsDto
import com.example.skillforge.domain.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class GradingState {
    object Loading : GradingState()
    data class Success(val details: QuizSubmissionDetailsDto) : GradingState()
    data class Error(val message: String) : GradingState()
    object SubmitSuccess : GradingState()
}

class GradingViewModel(
    private val quizRepo: QuizRepository,
    private val attemptId: String,
    private val token: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<GradingState>(GradingState.Loading)
    val uiState: StateFlow<GradingState> = _uiState

    init {
        loadDetails()
    }

    fun loadDetails() {
        viewModelScope.launch {
            _uiState.value = GradingState.Loading
            quizRepo.getSubmissionDetails(attemptId, "Bearer $token")
                .onSuccess { 
                    _uiState.value = GradingState.Success(it)
                }
                .onFailure { 
                    _uiState.value = GradingState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun submitGrade(questionGrades: List<com.example.skillforge.data.remote.dto.QuestionGradeDto>, feedback: String) {
        viewModelScope.launch {
            _uiState.value = GradingState.Loading
            quizRepo.gradeAttempt(attemptId, questionGrades, feedback, "Bearer $token")
                .onSuccess { 
                    _uiState.value = GradingState.SubmitSuccess
                }
                .onFailure { 
                    _uiState.value = GradingState.Error(it.message ?: "Failed to submit grade")
                }
        }
    }
}
