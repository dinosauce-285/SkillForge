package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillforge.domain.repository.QuizRepository

class SubmissionsViewModelFactory(
    private val quizRepo: QuizRepository,
    private val courseId: String,
    private val studentId: String,
    private val token: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubmissionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubmissionsViewModel(quizRepo, courseId, studentId, token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
