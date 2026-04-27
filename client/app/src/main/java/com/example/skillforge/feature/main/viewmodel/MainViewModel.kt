package com.example.skillforge.feature.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.core.navigation.AppRoute
import com.example.skillforge.domain.usecase.CheckSessionUseCase
import com.example.skillforge.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppRoute?>(null)
    val uiState: StateFlow<AppRoute?> = _uiState

    /**
     * Verifies the current session and navigates to the appropriate screen based on verified role.
     */
    fun checkSession() {
        viewModelScope.launch {
            val result = checkSessionUseCase()
            result.onSuccess { session ->
                val role = session.user.role.uppercase()
                
                when {
                    role == "STUDENT" -> {
                        _uiState.value = AppRoute.Home(session)
                    }
                    role == "INSTRUCTOR" -> {
                        _uiState.value = AppRoute.InstructorPortal(session)
                    }
                    else -> {
                        // If role is still UNKNOWN or unexpected (like "authenticated"), 
                        // stay on Login or show an error.
                        _uiState.value = AppRoute.Login
                    }
                }
            }.onFailure {
                _uiState.value = AppRoute.Login
            }
        }
    }

    fun navigateTo(route: AppRoute) {
        _uiState.value = route
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.value = AppRoute.Login
        }
    }
}
