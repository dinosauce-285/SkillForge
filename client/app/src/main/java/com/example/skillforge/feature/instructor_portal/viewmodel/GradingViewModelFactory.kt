package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillforge.domain.repository.QuizRepository

class GradingViewModelFactory(
    private val quizRepo: QuizRepository,
    private val attemptId: String,
    private val token: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GradingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GradingViewModel(quizRepo, attemptId, token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
