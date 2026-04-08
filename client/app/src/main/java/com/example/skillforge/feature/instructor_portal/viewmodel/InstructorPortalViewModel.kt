package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.CourseSummaryDto
import com.example.skillforge.domain.repository.CourseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class InstructorAnalyticsDto(
    val totalRevenue: Double,
    val revenueGrowth: Double,
    val newEnrollments: Int,
    val enrollmentsGrowth: Double,
    val studentSatisfaction: Double,
    val satisfactionRank: String
)

data class InstructorDashboardDto(
    val instructorName: String,
    val totalStudents: Int,
    val studentGrowth: String,
    val earnings: Double,
    val activeCourses: Int,
    val activities: List<ActivityData>,
    val courseCompletionRate: Float,
    val studentRetentionRate: Float
)

data class ActivityData(
    val iconType: String,
    val title: String,
    val description: String,
    val timeAgo: String
)

class InstructorPortalViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _courses = MutableStateFlow<List<CourseSummaryDto>>(emptyList())
    val courses: StateFlow<List<CourseSummaryDto>> = _courses

    private val _analyticsData = MutableStateFlow<InstructorAnalyticsDto?>(null)
    val analyticsData: StateFlow<InstructorAnalyticsDto?> = _analyticsData

    private val _dashboardData = MutableStateFlow<InstructorDashboardDto?>(null)
    val dashboardData: StateFlow<InstructorDashboardDto?> = _dashboardData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchAnalyticsData()
        fetchDashboardData()
    }

    fun fetchMyCourses(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            courseRepository.getMyCourses(token)
                .onSuccess { list ->
                    _courses.value = list
                }
                .onFailure {
                    _courses.value = emptyList()
                    it.printStackTrace()
                }
            _isLoading.value = false
        }
    }

    private fun fetchAnalyticsData() {
        viewModelScope.launch {
            delay(1000)
            _analyticsData.value = InstructorAnalyticsDto(
                totalRevenue = 12840.0,
                revenueGrowth = 12.4,
                newEnrollments = 1204,
                enrollmentsGrowth = 8.1,
                studentSatisfaction = 4.92,
                satisfactionRank = "TOP 5%"
            )
        }
    }

    private fun fetchDashboardData() {
        viewModelScope.launch {
            delay(1000)
            _dashboardData.value = InstructorDashboardDto(
                instructorName = "Curator",
                totalStudents = 1284,
                studentGrowth = "+12%",
                earnings = 42100.0,
                activeCourses = 14,
                courseCompletionRate = 0.78f,
                studentRetentionRate = 0.92f,
                activities = listOf(
                    ActivityData("comment", "New Discussion in\n\"Advanced Typography\"", "Elena Rossi commented on\nLesson 4", "2m ago"),
                    ActivityData("submission", "Project Submission", "14 students submitted \"Brand\nIdentity Design\"", "1h ago"),
                    ActivityData("review", "5-Star Review Received", "\"The most comprehensive\ncourse on UI/UX yet.\"", "4h ago")
                )
            )
        }
    }
}