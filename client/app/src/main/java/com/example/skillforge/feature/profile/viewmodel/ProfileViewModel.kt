package com.example.skillforge.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.dto.UpdateProfileRequestDTO
import com.example.skillforge.domain.usecase.GetProfileUseCase
import com.example.skillforge.domain.usecase.UpdateProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State representing the Profile UI.
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val fullName: String = "",
    val learningGoals: String = "",
    val skills: List<String> = emptyList(),
    val avatarUrl: String? = null,
    val errorMessage: String? = null,
    val isUpdateSuccessful: Boolean = false
)

/**
 * ProfileViewModel handles the business logic for the profile screen,
 * following Clean Architecture by interacting with UseCases.
 */
class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /**
     * Loads the user profile using GetProfileUseCase.
     */
    fun loadProfile(token: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            getProfileUseCase(token).fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fullName = user.fullName,
                            learningGoals = user.profile?.learningGoals ?: "",
                            skills = user.profile?.skills ?: emptyList(),
                            avatarUrl = user.profile?.avatarUrl
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Failed to load profile")
                    }
                }
            )
        }
    }

    // --- State Update Functions (State Hoisting) ---

    fun updateFullName(name: String) {
        _uiState.update { it.copy(fullName = name) }
    }

    fun updateLearningGoals(goals: String) {
        _uiState.update { it.copy(learningGoals = goals) }
    }

    fun addSkill(skill: String) {
        val currentSkills = _uiState.value.skills
        if (skill.isNotBlank() && !currentSkills.contains(skill.trim())) {
            _uiState.update { it.copy(skills = currentSkills + skill.trim()) }
        }
    }

    fun removeSkill(skillToRemove: String) {
        _uiState.update { currentState ->
            currentState.copy(skills = currentState.skills.filter { it != skillToRemove })
        }
    }

    fun resetSuccessFlag() {
        _uiState.update { it.copy(isUpdateSuccessful = false) }
    }

    /**
     * Saves the updated profile using UpdateProfileUseCase and UpdateProfileRequestDTO.
     */
    fun saveProfile(token: String) {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null, isUpdateSuccessful = false) }

        viewModelScope.launch {
            // Mapping UI State to Request DTO
            val requestDTO = UpdateProfileRequestDTO(
                fullName = currentState.fullName,
                avatarUrl = currentState.avatarUrl,
                skills = currentState.skills,
                learningGoals = currentState.learningGoals.ifBlank { null }
            )

            updateProfileUseCase(token, requestDTO).fold(
                onSuccess = { updatedUser ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isUpdateSuccessful = true,
                            fullName = updatedUser.fullName,
                            learningGoals = updatedUser.profile?.learningGoals ?: "",
                            skills = updatedUser.profile?.skills ?: emptyList(),
                            avatarUrl = updatedUser.profile?.avatarUrl
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Failed to update profile")
                    }
                }
            )
        }
    }
}
