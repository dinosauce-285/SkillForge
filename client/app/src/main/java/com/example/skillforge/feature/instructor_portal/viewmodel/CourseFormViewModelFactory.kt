package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillforge.domain.repository.CategoryRepository
import com.example.skillforge.domain.repository.CourseRepository

@Suppress("UNCHECKED_CAST")
class CourseFormViewModelFactory(
    private val repository: CourseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseFormViewModel::class.java)) {
            return CourseFormViewModel(repository, categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}