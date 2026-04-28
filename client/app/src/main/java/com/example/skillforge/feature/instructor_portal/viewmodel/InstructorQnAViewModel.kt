package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.InstructorDiscussionDto
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.domain.repository.LessonRepository
import com.example.skillforge.data.remote.CourseSummaryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class InstructorQnAState {
    object Loading : InstructorQnAState()
    data class Success(val discussions: List<InstructorDiscussionDto>) : InstructorQnAState()
    data class Error(val message: String) : InstructorQnAState()
}

class InstructorQnAViewModel(
    private val lessonRepository: LessonRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<InstructorQnAState>(InstructorQnAState.Loading)
    val uiState: StateFlow<InstructorQnAState> = _uiState.asStateFlow()

    private val _courses = MutableStateFlow<List<CourseSummaryDto>>(emptyList())
    val courses: StateFlow<List<CourseSummaryDto>> = _courses.asStateFlow()

    private val _selectedCourseId = MutableStateFlow<String?>(null)
    val selectedCourseId: StateFlow<String?> = _selectedCourseId.asStateFlow()

    private val _unansweredOnly = MutableStateFlow(true)
    val unansweredOnly: StateFlow<Boolean> = _unansweredOnly.asStateFlow()

    private var currentToken: String = ""

    fun initialize(token: String) {
        currentToken = token
        fetchCourses()
        fetchDiscussions()
    }

    private fun fetchCourses() {
        viewModelScope.launch {
            val result = courseRepository.getMyCourses(currentToken)
            result.onSuccess { courseList ->
                _courses.value = courseList
            }.onFailure {
                // Silently fail fetching courses, fallback to no courses
            }
        }
    }

    fun fetchDiscussions() {
        viewModelScope.launch {
            _uiState.value = InstructorQnAState.Loading
            val result = lessonRepository.getInstructorDiscussions(
                token = currentToken,
                courseId = _selectedCourseId.value,
                unansweredOnly = _unansweredOnly.value
            )
            result.onSuccess { discussions ->
                _uiState.value = InstructorQnAState.Success(discussions)
            }.onFailure { error ->
                _uiState.value = InstructorQnAState.Error(error.message ?: "Failed to fetch questions")
            }
        }
    }

    fun setCourseFilter(courseId: String?) {
        _selectedCourseId.value = courseId
        fetchDiscussions()
    }

    fun toggleUnansweredOnly() {
        _unansweredOnly.value = !_unansweredOnly.value
        fetchDiscussions()
    }

    fun replyToQuestion(discussionId: String, lessonId: String, content: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = lessonRepository.replyToDiscussion(
                token = currentToken,
                discussionId = discussionId,
                lessonId = lessonId,
                content = content
            )
            result.onSuccess {
                // Re-fetch to reflect updated replies
                fetchDiscussions()
                onSuccess()
            }.onFailure { error ->
                // Basic error handling
            }
        }
    }
}

class InstructorQnAViewModelFactory(
    private val lessonRepository: LessonRepository,
    private val courseRepository: CourseRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstructorQnAViewModel::class.java)) {
            return InstructorQnAViewModel(lessonRepository, courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
