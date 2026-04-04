package com.example.skillforge.feature.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(fullName: String, email: String, password: String) {
        Log.d("API_DEBUG", "1. Register button triggered ViewModel")
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            val result = registerUseCase(fullName, email, password)

            result.onSuccess { token ->
                _registerState.value = RegisterState.Success(token)
            }
            result.onFailure { exception ->
                _registerState.value = RegisterState.Error(exception.message ?: "Unknown error")
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            try {
                registerUseCase.loginWithGoogle()
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Google sign-in failed")
            }
        }
    }
}