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
    val errorMessage: String? = null,
)

class FavoriteViewModel(
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavoriteUiState(isLoading = true))
    val uiState: StateFlow<FavoriteUiState> = _uiState

    fun loadFavorites(token: String) {
        viewModelScope.launch {
            _uiState.value = FavoriteUiState(isLoading = true)

            favoriteRepository.getFavorites(token).fold(
                onSuccess = { courses ->
                    _uiState.value = FavoriteUiState(
                        isLoading = false,
                        courses = courses,
                    )
                },
                onFailure = { error ->
                    _uiState.value = FavoriteUiState(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load favorites",
                    )
                },
            )
        }
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
