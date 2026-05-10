package com.example.skillforge.feature.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.FavoriteCourse
import com.example.skillforge.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val courses: List<FavoriteCourse> = emptyList(),
    val selectedCourseIds: Set<String> = emptySet(),
    val errorMessage: String? = null,
    val actionMessage: String? = null,
)

class FavoriteViewModel(
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavoriteUiState(isLoading = true))
    val uiState: StateFlow<FavoriteUiState> = _uiState

    fun loadFavorites(token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            favoriteRepository.getFavorites(token).fold(
                onSuccess = { courses ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        courses = courses,
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load favorites",
                    )
                },
            )
        }
    }

    fun toggleSelection(courseId: String) {
        val currentSelected = _uiState.value.selectedCourseIds
        val newSelected = if (currentSelected.contains(courseId)) {
            currentSelected - courseId
        } else {
            currentSelected + courseId
        }
        _uiState.value = _uiState.value.copy(selectedCourseIds = newSelected)
    }

    fun selectAll(select: Boolean) {
        val newSelected = if (select) {
            _uiState.value.courses.map { it.id }.toSet()
        } else {
            emptySet()
        }
        _uiState.value = _uiState.value.copy(selectedCourseIds = newSelected)
    }

    fun addFavorite(token: String, courseId: String) {
        viewModelScope.launch {
            favoriteRepository.addFavorite(token, courseId).fold(
                onSuccess = {
                    loadFavorites(token)
                    _uiState.value = _uiState.value.copy(actionMessage = "Added to wishlist")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
            )
        }
    }

    fun removeFavorite(token: String, courseId: String) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(token, courseId).fold(
                onSuccess = {
                    loadFavorites(token)
                    _uiState.value = _uiState.value.copy(actionMessage = "Removed from wishlist")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
            )
        }
    }

    fun clearActionMessage() {
        _uiState.value = _uiState.value.copy(actionMessage = null)
    }
}

class FavoriteViewModelFactory(
    private val favoriteRepository: FavoriteRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(favoriteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
