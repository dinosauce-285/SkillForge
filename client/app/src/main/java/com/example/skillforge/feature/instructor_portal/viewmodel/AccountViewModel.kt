package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.UserInfo
import com.example.skillforge.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AccountState {
    object Loading : AccountState()
    data class Success(val userInfo: UserInfo) : AccountState()
    data class Error(val message: String) : AccountState()
}

class AccountViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountState>(AccountState.Loading)
    val uiState: StateFlow<AccountState> = _uiState

    fun loadAccountInfo() {
        viewModelScope.launch {
            _uiState.value = AccountState.Loading
            authRepository.getMe()
                .onSuccess { user ->
                    _uiState.value = AccountState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = AccountState.Error(error.message ?: "Unknown error")
                }
        }
    }
}