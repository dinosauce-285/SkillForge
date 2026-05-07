package com.example.skillforge.feature.student_courses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.Quiz
import com.example.skillforge.domain.model.Question
import com.example.skillforge.domain.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.Collections

data class StudentQuizUiState(
    val isLoading: Boolean = false,
    val quiz: Quiz? = null,
    val shuffledQuestions: List<Question> = emptyList(),
    val errorMessage: String? = null,
    val timeRemainingSeconds: Int = 0,
    val isTimerRunning: Boolean = false
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

    private var timerJob: Job? = null

    fun loadQuiz(quizId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            _submissionResult.value = null
            stopTimer()
            
            quizRepository.getQuizById(quizId).fold(
                onSuccess = { quiz ->
                    val questions = if (quiz.randomizeQuestions) {
                        quiz.questions.shuffled()
                    } else {
                        quiz.questions
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        quiz = quiz,
                        shuffledQuestions = questions,
                        timeRemainingSeconds = quiz.timeLimit * 60,
                        isTimerRunning = true
                    )
                    startTimer()
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

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemainingSeconds > 0) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    timeRemainingSeconds = _uiState.value.timeRemainingSeconds - 1
                )
            }
            // Auto-submit when time is up? Or handle in UI.
            // For now, just stop timer.
            _uiState.value = _uiState.value.copy(isTimerRunning = false)
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerRunning = false)
    }

    fun submitQuiz(answers: Map<String, String>) {
        val quizId = _uiState.value.quiz?.id ?: return
        stopTimer()
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

    override fun onCleared() {
        super.onCleared()
        stopTimer()
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
