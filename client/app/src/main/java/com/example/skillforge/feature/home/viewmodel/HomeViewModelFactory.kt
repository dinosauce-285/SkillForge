package com.example.skillforge.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillforge.domain.repository.ProgressRepository

class HomeViewModelFactory(
    private val progressRepository: ProgressRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(progressRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}