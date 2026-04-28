package com.example.skillforge.feature.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.HomeDashboard
import com.example.skillforge.domain.model.Notification
import com.example.skillforge.domain.repository.NotificationRepository
import com.example.skillforge.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val dashboard: HomeDashboard) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val isNotificationLoading: Boolean = false,
    val errorMessage: String? = null,
)

private const val HOME_VIEW_MODEL_TAG = "HomeViewModel"

class HomeViewModel(
    private val progressRepository: ProgressRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _notificationState = MutableStateFlow(NotificationUiState())
    val notificationState: StateFlow<NotificationUiState> = _notificationState.asStateFlow()

    fun fetchDashboard(token: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val result = progressRepository.getDashboardProgress(token)
                _uiState.value = HomeUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun fetchNotifications() {
        viewModelScope.launch {
            _notificationState.value = _notificationState.value.copy(
                isNotificationLoading = true,
                errorMessage = null,
            )

            try {
                notificationRepository.getNotifications()
                    .onSuccess { result ->
                        _notificationState.value = NotificationUiState(
                            notifications = result.notifications,
                            unreadCount = result.unreadCount,
                            isNotificationLoading = false,
                        )
                    }
                    .onFailure { error ->
                        Log.e(HOME_VIEW_MODEL_TAG, "Failed to load notifications", error)
                        _notificationState.value = _notificationState.value.copy(
                            isNotificationLoading = false,
                            errorMessage = error.message ?: "Failed to load notifications",
                        )
                    }
            } catch (e: Exception) {
                Log.e(HOME_VIEW_MODEL_TAG, "Failed to load notifications", e)
                _notificationState.value = _notificationState.value.copy(
                    isNotificationLoading = false,
                    errorMessage = e.message ?: "Failed to load notifications",
                )
            }
        }
    }

    fun markAsRead(id: String) {
        val currentState = _notificationState.value
        val notification = currentState.notifications.firstOrNull { it.id == id } ?: return
        if (notification.isRead) return

        _notificationState.value = currentState.copy(
            notifications = currentState.notifications.map {
                if (it.id == id) it.copy(readAt = "read") else it
            },
            unreadCount = (currentState.unreadCount - 1).coerceAtLeast(0),
        )

        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(id)
                    .onFailure { error ->
                        Log.e(HOME_VIEW_MODEL_TAG, "Failed to mark notification as read", error)
                        fetchNotifications()
                    }
            } catch (e: Exception) {
                Log.e(HOME_VIEW_MODEL_TAG, "Failed to mark notification as read", e)
                fetchNotifications()
            }
        }
    }

    fun markAllAsRead() {
        val currentState = _notificationState.value
        if (currentState.unreadCount == 0) return

        _notificationState.value = currentState.copy(
            notifications = currentState.notifications.map { notification ->
                if (notification.isRead) notification else notification.copy(readAt = "read")
            },
            unreadCount = 0,
        )

        viewModelScope.launch {
            try {
                notificationRepository.markAllAsRead()
                    .onFailure { error ->
                        Log.e(HOME_VIEW_MODEL_TAG, "Failed to mark all notifications as read", error)
                        fetchNotifications()
                    }
            } catch (e: Exception) {
                Log.e(HOME_VIEW_MODEL_TAG, "Failed to mark all notifications as read", e)
                fetchNotifications()
            }
        }
    }
}
