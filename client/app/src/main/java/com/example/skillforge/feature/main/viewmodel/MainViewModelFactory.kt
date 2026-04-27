package com.example.skillforge.feature.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillforge.domain.usecase.CheckSessionUseCase
import com.example.skillforge.domain.usecase.LogoutUseCase

class MainViewModelFactory(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(checkSessionUseCase, logoutUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
