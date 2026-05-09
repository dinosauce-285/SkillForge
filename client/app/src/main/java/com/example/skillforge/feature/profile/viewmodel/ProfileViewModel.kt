package com.example.skillforge.feature.profile.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.dto.UpdateProfileRequestDTO
import com.example.skillforge.domain.model.User
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
        val role: String = "",
        val isEditing: Boolean = false,
        val isSaving: Boolean = false,
        val isUploadingAvatar: Boolean = false,
        val inlineErrorMessage: String? = null,
        val isUpdateSuccessful: Boolean = false
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

private data class ProfileSnapshot(
    val id: String,
    val email: String,
    val fullName: String,
    val headline: String,
    val learningGoals: String,
    val skills: List<String>,
    val avatarUrl: String?,
    val role: String,
    val isActive: Boolean
)

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

    private var committedProfile: ProfileSnapshot? = null
    private var draftProfile: ProfileSnapshot? = null

    /**
     * Loads the user profile.
     */
    fun loadProfile() {
        if (committedProfile == null) {
            _uiState.value = ProfileUiState.Loading
        }

        viewModelScope.launch {
            getProfileUseCase().fold(
                onSuccess = { user ->
                    committedProfile = user.toProfileSnapshot()
                    draftProfile = committedProfile
                    updateUiWithCurrentData()
                },
                onFailure = { error ->
                    val message = error.message ?: "Failed to load profile"
                    if (committedProfile == null) {
                        _uiState.value = ProfileUiState.Error(message)
                    } else {
                        updateUiWithCurrentData(inlineErrorMessage = message)
                    }
                }
            )
        }
    }

    fun retryLoadProfile() {
        loadProfile()
    }

    fun startEditing() {
        val profile = committedProfile ?: return
        draftProfile = profile
        updateUiWithCurrentData(isEditing = true)
    }

    fun cancelEditing() {
        draftProfile = committedProfile
        updateUiWithCurrentData(isEditing = false)
    }

    /**
     * Uploads a new avatar image.
     */
    fun uploadAvatar(uri: Uri, contentResolver: ContentResolver) {
        val state = _uiState.value
        val wasEditing = state is ProfileUiState.Success && state.isEditing
        updateUiWithCurrentData(isEditing = wasEditing, isUploadingAvatar = true)

        viewModelScope.launch {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    val fileName = "avatar_${System.currentTimeMillis()}.jpg"
                    updateAvatarUseCase(bytes, fileName).fold(
                        onSuccess = { newUrl ->
                            val target = if (wasEditing) draftProfile else committedProfile
                            val updated = target?.copy(avatarUrl = newUrl)
                            if (wasEditing) {
                                draftProfile = updated
                            } else {
                                committedProfile = updated
                                draftProfile = updated
                            }
                            updateUiWithCurrentData(isEditing = wasEditing)
                        },
                        onFailure = { error ->
                            updateUiWithCurrentData(
                                isEditing = wasEditing,
                                inlineErrorMessage = error.message ?: "Upload failed"
                            )
                        }
                    )
                } else {
                    updateUiWithCurrentData(
                        isEditing = wasEditing,
                        inlineErrorMessage = "Could not read image data"
                    )
                }
            } catch (e: Exception) {
                updateUiWithCurrentData(
                    isEditing = wasEditing,
                    inlineErrorMessage = "Error processing image: ${e.message}"
                )
            }
        }
    }

    /**
     * Updates profile information.
     */
    fun saveChanges() {
        val profile = draftProfile ?: return
        updateUiWithCurrentData(isEditing = true, isSaving = true)

        viewModelScope.launch {
            val requestDTO = UpdateProfileRequestDTO(
                fullName = profile.fullName,
                avatarUrl = profile.avatarUrl,
                skills = profile.skills,
                learningGoals = profile.learningGoals.ifBlank { null }
            )

            updateProfileUseCase(requestDTO).fold(
                onSuccess = { updatedUser ->
                    committedProfile = updatedUser.toProfileSnapshot(fallback = profile)
                    draftProfile = committedProfile
                    updateUiWithCurrentData(isUpdateSuccessful = true)
                },
                onFailure = { error ->
                    updateUiWithCurrentData(
                        isEditing = true,
                        inlineErrorMessage = error.message ?: "Update failed"
                    )
                }
            )
        }
    }

    fun updateProfile() {
        saveChanges()
    }

    // --- Data Management for UI ---

    fun onFullNameChange(name: String) {
        draftProfile = (draftProfile ?: committedProfile)?.copy(fullName = name)
        updateUiWithCurrentData(isEditing = true)
    }

    fun onLearningGoalsChange(goals: String) {
        draftProfile = (draftProfile ?: committedProfile)?.copy(learningGoals = goals)
        updateUiWithCurrentData(isEditing = true)
    }

    fun addSkill(skill: String) {
        val trimmedSkill = skill.trim()
        val profile = draftProfile ?: committedProfile ?: return
        if (trimmedSkill.isNotBlank() && profile.skills.none { it.equals(trimmedSkill, ignoreCase = true) }) {
            draftProfile = profile.copy(skills = profile.skills + trimmedSkill)
            updateUiWithCurrentData(isEditing = true)
        }
    }

    fun removeSkill(skill: String) {
        val profile = draftProfile ?: committedProfile ?: return
        draftProfile = profile.copy(skills = profile.skills.filter { it != skill })
        updateUiWithCurrentData(isEditing = true)
    }

    private fun updateUiWithCurrentData(
        isEditing: Boolean = false,
        isSaving: Boolean = false,
        isUploadingAvatar: Boolean = false,
        inlineErrorMessage: String? = null,
        isUpdateSuccessful: Boolean = false
    ) {
        val profile = if (isEditing) {
            draftProfile ?: committedProfile
        } else {
            committedProfile
        } ?: return

        _uiState.value = ProfileUiState.Success(
            fullName = profile.fullName,
            headline = profile.headline,
            learningGoals = profile.learningGoals,
            skills = profile.skills,
            avatarUrl = profile.avatarUrl,
            role = profile.role,
            isEditing = isEditing,
            isSaving = isSaving,
            isUploadingAvatar = isUploadingAvatar,
            inlineErrorMessage = inlineErrorMessage,
            isUpdateSuccessful = isUpdateSuccessful
        )
    }

    private fun User.toProfileSnapshot(fallback: ProfileSnapshot? = null): ProfileSnapshot {
        val resolvedRole = role.takeIf { it.isNotBlank() } ?: fallback?.role.orEmpty()
        val resolvedProfile = profile
        val resolvedLearningGoals = resolvedProfile?.learningGoals ?: fallback?.learningGoals.orEmpty()
        val resolvedSkills = resolvedProfile?.skills ?: fallback?.skills.orEmpty()
        val resolvedAvatarUrl = resolvedProfile?.avatarUrl ?: fallback?.avatarUrl
        val headline = if (resolvedRole.equals("INSTRUCTOR", ignoreCase = true)) {
            "Instructor"
        } else if (resolvedRole.equals("ADMIN", ignoreCase = true)) {
            "Admin"
        } else {
            "Student"
        }

        return ProfileSnapshot(
            id = id.takeIf { it.isNotBlank() } ?: fallback?.id.orEmpty(),
            email = email.takeIf { it.isNotBlank() } ?: fallback?.email.orEmpty(),
            fullName = fullName.takeIf { it.isNotBlank() } ?: fallback?.fullName.orEmpty(),
            headline = headline,
            learningGoals = resolvedLearningGoals,
            skills = resolvedSkills,
            avatarUrl = resolvedAvatarUrl,
            role = resolvedRole,
            isActive = fallback?.isActive ?: isActive
        )
    }
}
