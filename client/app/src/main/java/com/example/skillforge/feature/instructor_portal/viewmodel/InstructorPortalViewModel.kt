package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.CourseSummaryDto
import com.example.skillforge.data.remote.InstructorDashboardDto
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InstructorPortalViewModel(
    private val courseRepository: CourseRepository,
    private val dashboardRepository: DashboardRepository // Add this parameter
) : ViewModel() {

    private val _courses = MutableStateFlow<List<CourseSummaryDto>>(emptyList())
    val courses: StateFlow<List<CourseSummaryDto>> = _courses

    private val _dashboardData = MutableStateFlow<InstructorDashboardDto?>(null)
    val dashboardData: StateFlow<InstructorDashboardDto?> = _dashboardData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchMyCourses(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            courseRepository.getMyCourses(token)
                .onSuccess { list -> _courses.value = list }
                .onFailure { _courses.value = emptyList() }
            _isLoading.value = false
        }
    }

    fun fetchDashboardData(token: String) {
        viewModelScope.launch {
            dashboardRepository.getInstructorDashboard(token)
                .onSuccess { data -> _dashboardData.value = data }
                .onFailure { it.printStackTrace() }
        }
    }
}