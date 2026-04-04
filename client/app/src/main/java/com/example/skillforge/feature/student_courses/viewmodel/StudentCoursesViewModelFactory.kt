package com.example.skillforge.feature.student_courses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillforge.domain.repository.CategoryRepository
import com.example.skillforge.domain.repository.CourseRepository

class StudentCoursesViewModelFactory(
    private val courseRepository: CourseRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentCoursesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudentCoursesViewModel(courseRepository, categoryRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
