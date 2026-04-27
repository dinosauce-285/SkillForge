package com.example.skillforge.feature.profile.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.dto.UpdateProfileRequestDTO
import com.example.skillforge.domain.usecase.GetProfileUseCase
import com.example.skillforge.domain.usecase.UpdateAvatarUseCase
import com.example.skillforge.domain.usecase.UpdateProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Sealed class representing the different states of the Profile UI.
 */
sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(
        val fullName: String,
        val headline: String,
        val learningGoals: String,
        val skills: List<String>,
        val avatarUrl: String?,
        val isUpdateSuccessful: Boolean = false
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

/**
 * ProfileViewModel handles the business logic for the profile screen.
 * It coordinates profile data fetching, updating, and avatar uploading.
 */
class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Temporary storage for fields while editing
    private var currentFullName: String = ""
    private var currentHeadline: String = "Student"
    private var currentLearningGoals: String = ""
    private var currentSkills: List<String> = emptyList()
    private var currentAvatarUrl: String? = null

    /**
     * Loads the user profile.
     */
    fun loadProfile() {
        _uiState.value = ProfileUiState.Loading

        viewModelScope.launch {
            getProfileUseCase().fold(
                onSuccess = { user ->
                    currentFullName = user.fullName
                    currentHeadline = if (user.role.equals("INSTRUCTOR", ignoreCase = true)) "Instructor" else "Student"
                    currentLearningGoals = user.profile?.learningGoals ?: ""
                    currentSkills = user.profile?.skills ?: emptyList()
                    currentAvatarUrl = user.profile?.avatarUrl

                    _uiState.value = ProfileUiState.Success(
                        fullName = currentFullName,
                        headline = currentHeadline,
                        learningGoals = currentLearningGoals,
                        skills = currentSkills,
                        avatarUrl = currentAvatarUrl
                    )
                },
                onFailure = { error ->
                    _uiState.value = ProfileUiState.Error(error.message ?: "Failed to load profile")
                }
            )
        }
    }

    /**
     * Uploads a new avatar image.
     */
    fun uploadAvatar(uri: Uri, contentResolver: ContentResolver) {
        _uiState.value = ProfileUiState.Loading

        viewModelScope.launch {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    val fileName = "avatar_${System.currentTimeMillis()}.jpg"
                    updateAvatarUseCase(bytes, fileName).fold(
                        onSuccess = { newUrl ->
                            currentAvatarUrl = newUrl
                            updateUiWithCurrentData()
                        },
                        onFailure = { error ->
                            _uiState.value = ProfileUiState.Error(error.message ?: "Upload failed")
                        }
                    )
                } else {
                    _uiState.value = ProfileUiState.Error("Could not read image data")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Error processing image: ${e.message}")
            }
        }
    }

    /**
     * Updates profile information.
     */
    fun updateProfile() {
        _uiState.value = ProfileUiState.Loading

        viewModelScope.launch {
            val requestDTO = UpdateProfileRequestDTO(
                fullName = currentFullName,
                avatarUrl = currentAvatarUrl,
                skills = currentSkills,
                learningGoals = currentLearningGoals.ifBlank { null }
            )

            updateProfileUseCase(requestDTO).fold(
                onSuccess = { updatedUser ->
                    currentFullName = updatedUser.fullName
                    currentHeadline = if (updatedUser.role.equals("INSTRUCTOR", ignoreCase = true)) "Instructor" else "Student"
                    currentLearningGoals = updatedUser.profile?.learningGoals ?: ""
                    currentSkills = updatedUser.profile?.skills ?: emptyList()
                    currentAvatarUrl = updatedUser.profile?.avatarUrl

                    _uiState.value = ProfileUiState.Success(
                        fullName = currentFullName,
                        headline = currentHeadline,
                        learningGoals = currentLearningGoals,
                        skills = currentSkills,
                        avatarUrl = currentAvatarUrl,
                        isUpdateSuccessful = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = ProfileUiState.Error(error.message ?: "Update failed")
                }
            )
        }
    }

    // --- Data Management for UI ---

    fun onFullNameChange(name: String) {
        currentFullName = name
        updateUiWithCurrentData()
    }

    fun onLearningGoalsChange(goals: String) {
        currentLearningGoals = goals
        updateUiWithCurrentData()
    }

    fun addSkill(skill: String) {
        if (skill.isNotBlank() && !currentSkills.contains(skill.trim())) {
            currentSkills = currentSkills + skill.trim()
            updateUiWithCurrentData()
        }
    }

    fun removeSkill(skill: String) {
        currentSkills = currentSkills.filter { it != skill }
        updateUiWithCurrentData()
    }

    private fun updateUiWithCurrentData() {
        _uiState.value = ProfileUiState.Success(
            fullName = currentFullName,
            headline = currentHeadline,
            learningGoals = currentLearningGoals,
            skills = currentSkills,
            avatarUrl = currentAvatarUrl
        )
    }
}
