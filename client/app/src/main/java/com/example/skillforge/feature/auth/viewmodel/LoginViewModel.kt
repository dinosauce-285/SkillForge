package com.example.skillforge.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.AuthSession
import com.example.skillforge.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val session: AuthSession) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val result = loginUseCase(email, password)

            result.onSuccess { session ->
                _loginState.value = LoginState.Success(session)
            }
            result.onFailure { exception ->
                _loginState.value = LoginState.Error(exception.message ?: "Lỗi không xác định")
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            try {
                loginUseCase.loginWithGoogle()
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Lỗi đăng nhập Google")
            }
        }
    }

    fun clearError() {
        if (_loginState.value is LoginState.Error) {
            _loginState.value = LoginState.Idle
        }
    }
}
