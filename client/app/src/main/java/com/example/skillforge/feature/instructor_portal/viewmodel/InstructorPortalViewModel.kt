package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.CourseSummaryDto
import com.example.skillforge.domain.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InstructorPortalViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    // translated comment
    private val _courses = MutableStateFlow<List<CourseSummaryDto>>(emptyList())
    val courses: StateFlow<List<CourseSummaryDto>> = _courses

    // translated comment
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchMyCourses(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            courseRepository.getMyCourses(token)
                .onSuccess { danhSach ->
                    _courses.value = danhSach
                    // translated comment
                    android.util.Log.d("KiemTra", "Successfully loaded ${danhSach.size} courses!")
                }
                .onFailure {
                    _courses.value = emptyList()
                    // translated comment
                    android.util.Log.e("KiemTra", "Network failure or data type mismatch: ${it.message}")
                    it.printStackTrace()
                }
            _isLoading.value = false
        }
    }
}
