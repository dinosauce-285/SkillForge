package com.example.skillforge.feature.subscription.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.InstructorSubscriptionActivation
import com.example.skillforge.domain.usecase.BecomeInstructorUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class InstructorSubscriptionUiState {
    object Idle : InstructorSubscriptionUiState()
    object Loading : InstructorSubscriptionUiState()
    data class Success(
        val activation: InstructorSubscriptionActivation,
    ) : InstructorSubscriptionUiState()
    data class Error(
        val message: String,
    ) : InstructorSubscriptionUiState()
}

class InstructorSubscriptionViewModel(
    private val becomeInstructorUseCase: BecomeInstructorUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<InstructorSubscriptionUiState>(
        InstructorSubscriptionUiState.Idle,
    )
    val uiState: StateFlow<InstructorSubscriptionUiState> = _uiState.asStateFlow()

    fun confirmMockPayment() {
        if (_uiState.value is InstructorSubscriptionUiState.Loading) return

        viewModelScope.launch {
            _uiState.value = InstructorSubscriptionUiState.Loading

            becomeInstructorUseCase().fold(
                onSuccess = { activation ->
                    _uiState.value = InstructorSubscriptionUiState.Success(activation)
                },
                onFailure = { error ->
                    _uiState.value = InstructorSubscriptionUiState.Error(
                        error.message ?: "Failed to activate instructor subscription",
                    )
                },
            )
        }
    }

    fun clearError() {
        if (_uiState.value is InstructorSubscriptionUiState.Error) {
            _uiState.value = InstructorSubscriptionUiState.Idle
        }
    }
}
