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

    // Danh sách khóa học
    private val _courses = MutableStateFlow<List<CourseSummaryDto>>(emptyList())
    val courses: StateFlow<List<CourseSummaryDto>> = _courses

    // Trạng thái đang tải
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchMyCourses(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            courseRepository.getMyCourses(token)
                .onSuccess { danhSach ->
                    _courses.value = danhSach
                    // 🌟 Thêm dòng này để xem có lấy được cục data nào không
                    android.util.Log.d("KiemTra", "✅ Lấy thành công ${danhSach.size} khóa học!")
                }
                .onFailure {
                    _courses.value = emptyList()
                    // 🌟 In đỏ chót ra Logcat để xem nguyên nhân
                    android.util.Log.e("KiemTra", "❌ Lỗi rớt mạng hoặc sai kiểu dữ liệu: ${it.message}")
                    it.printStackTrace()
                }
            _isLoading.value = false
        }
    }
}