package com.example.skillforge.feature.subscription.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillforge.domain.usecase.BecomeInstructorUseCase

class InstructorSubscriptionViewModelFactory(
    private val becomeInstructorUseCase: BecomeInstructorUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstructorSubscriptionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InstructorSubscriptionViewModel(becomeInstructorUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
