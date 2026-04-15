package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.domain.repository.DashboardRepository

@Suppress("UNCHECKED_CAST")

class InstructorPortalViewModelFactory(
    private val courseRepository: CourseRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstructorPortalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InstructorPortalViewModel(courseRepository, dashboardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}