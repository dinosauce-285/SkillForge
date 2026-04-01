package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.CreateChapterRequest
import com.example.skillforge.data.remote.CreateLessonRequest
import com.example.skillforge.data.remote.CourseManagerDto
import com.example.skillforge.domain.repository.ChapterRepository
import com.example.skillforge.domain.repository.CourseRepository // 🌟 Dùng hàng chính chủ
import com.example.skillforge.domain.repository.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CourseManagerState {
    object Loading : CourseManagerState()
    data class Success(val course: CourseManagerDto) : CourseManagerState()
    data class Error(val message: String) : CourseManagerState()
}

class CourseManagerViewModel(
    private val courseRepo: CourseRepository, // 🌟 Nhận CourseRepository
    private val chapterRepo: ChapterRepository,
    private val lessonRepo: LessonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CourseManagerState>(CourseManagerState.Loading)
    val uiState: StateFlow<CourseManagerState> = _uiState

    private var currentToken: String = ""
    private var currentCourseId: String = ""

    fun loadCourseStructure(token: String, courseId: String) {
        currentToken = token
        currentCourseId = courseId
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _uiState.value = CourseManagerState.Loading
            // 🌟 Gọi hàm từ CourseRepo
            courseRepo.getCourseForManager(currentToken, currentCourseId)
                .onSuccess { _uiState.value = CourseManagerState.Success(it) }
                .onFailure { _uiState.value = CourseManagerState.Error(it.message ?: "Lỗi tải dữ liệu") }
        }
    }

    fun createChapter(title: String) {
        if (title.isBlank() || currentToken.isEmpty() || currentCourseId.isEmpty()) return

        viewModelScope.launch {
            val request = CreateChapterRequest(courseId = currentCourseId, title = title)
            chapterRepo.createChapter(currentToken, request)
                .onSuccess { fetchData() }
                .onFailure { /* Có thể log lỗi */ }
        }
    }

    fun createLesson(chapterId: String, title: String) {
        if (title.isBlank() || currentToken.isEmpty()) return

        viewModelScope.launch {
            val request = CreateLessonRequest(chapterId = chapterId, title = title)
            lessonRepo.createLesson(currentToken, request)
                .onSuccess { fetchData() }
                .onFailure { /* Có thể log lỗi */ }
        }
    }
}