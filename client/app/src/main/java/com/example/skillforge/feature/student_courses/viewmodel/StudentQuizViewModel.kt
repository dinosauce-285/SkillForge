package com.example.skillforge.feature.student_courses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.Quiz
import com.example.skillforge.domain.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class StudentQuizUiState(
    val isLoading: Boolean = false,
    val quiz: Quiz? = null,
    val errorMessage: String? = null
)

class StudentQuizViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentQuizUiState())
    val uiState: StateFlow<StudentQuizUiState> = _uiState

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _submissionResult = MutableStateFlow<com.example.skillforge.domain.model.QuizSubmissionResult?>(null)
    val submissionResult: StateFlow<com.example.skillforge.domain.model.QuizSubmissionResult?> = _submissionResult

    fun loadQuiz(quizId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            _submissionResult.value = null
            quizRepository.getQuizById(quizId).fold(
                onSuccess = { quiz ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        quiz = quiz
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load quiz"
                    )
                }
            )
        }
    }

    fun submitQuiz(answers: Map<String, String>) {
        val quizId = _uiState.value.quiz?.id ?: return
        viewModelScope.launch {
            _isSubmitting.value = true
            quizRepository.submitQuiz(quizId, answers).fold(
                onSuccess = { result ->
                    _isSubmitting.value = false
                    _submissionResult.value = result
                },
                onFailure = { error ->
                    _isSubmitting.value = false
                    _uiState.value = _uiState.value.copy(errorMessage = error.message ?: "Failed to submit quiz")
                }
            )
        }
    }
    
    fun resetSubmission() {
        _submissionResult.value = null
    }
}

class StudentQuizViewModelFactory(
    private val quizRepository: QuizRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentQuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudentQuizViewModel(quizRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
