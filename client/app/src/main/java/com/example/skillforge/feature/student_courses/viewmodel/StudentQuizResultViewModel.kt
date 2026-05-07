package com.example.skillforge.feature.student_courses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.dto.QuizSubmissionDetailsDto
import com.example.skillforge.domain.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class StudentQuizResultState {
    object Loading : StudentQuizResultState()
    data class Success(val details: QuizSubmissionDetailsDto) : StudentQuizResultState()
    data class Error(val message: String) : StudentQuizResultState()
}

class StudentQuizResultViewModel(
    private val quizRepo: QuizRepository,
    private val attemptId: String,
    private val token: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentQuizResultState>(StudentQuizResultState.Loading)
    val uiState: StateFlow<StudentQuizResultState> = _uiState

    init {
        loadResult()
    }

    fun loadResult() {
        viewModelScope.launch {
            _uiState.value = StudentQuizResultState.Loading
            quizRepo.getMySubmission(attemptId, "Bearer $token")
                .onSuccess { 
                    _uiState.value = StudentQuizResultState.Success(it)
                }
                .onFailure { 
                    _uiState.value = StudentQuizResultState.Error(it.message ?: "Unknown error")
                }
        }
    }
}

class StudentQuizResultViewModelFactory(
    private val quizRepo: QuizRepository,
    private val attemptId: String,
    private val token: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentQuizResultViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudentQuizResultViewModel(quizRepo, attemptId, token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
