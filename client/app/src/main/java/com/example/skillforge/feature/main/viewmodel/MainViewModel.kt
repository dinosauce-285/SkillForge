package com.example.skillforge.feature.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.core.navigation.AppRoute
import com.example.skillforge.domain.usecase.CheckSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val checkSessionUseCase: CheckSessionUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<AppRoute?>(null)
    val uiState: StateFlow<AppRoute?> = _uiState

    fun checkSession() {
        viewModelScope.launch {
            val result = checkSessionUseCase()
            result.onSuccess { session ->
                _uiState.value = if (session.user.role.equals("STUDENT", ignoreCase = true)) {
                    AppRoute.Home(session)
                } else {
                    AppRoute.InstructorPortal(session)
                }
            }.onFailure {
                _uiState.value = AppRoute.Login
            }
        }
    }

    fun navigateTo(route: AppRoute) {
        _uiState.value = route
    }
}
