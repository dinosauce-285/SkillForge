package com.example.skillforge.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillforge.core.di.AppContainer

/**
 * Factory for creating ProfileViewModel with required dependencies.
 */
class ProfileViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(
                getProfileUseCase = appContainer.getProfileUseCase,
                updateProfileUseCase = appContainer.updateProfileUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
