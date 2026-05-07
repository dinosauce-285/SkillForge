package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.dto.QuizSubmissionDto
import com.example.skillforge.domain.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SubmissionsState {
    object Loading : SubmissionsState()
    data class Success(val submissions: List<QuizSubmissionDto>) : SubmissionsState()
    data class Error(val message: String) : SubmissionsState()
}

class SubmissionsViewModel(
    private val quizRepo: QuizRepository,
    private val courseId: String,
    private val studentId: String,
    private val token: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubmissionsState>(SubmissionsState.Loading)
    val uiState: StateFlow<SubmissionsState> = _uiState

    init {
        loadSubmissions()
    }

    fun loadSubmissions() {
        viewModelScope.launch {
            _uiState.value = SubmissionsState.Loading
            quizRepo.getSubmissions(courseId, studentId, "Bearer $token")
                .onSuccess { 
                    _uiState.value = SubmissionsState.Success(it)
                }
                .onFailure { 
                    _uiState.value = SubmissionsState.Error(it.message ?: "Unknown error")
                }
        }
    }
}
