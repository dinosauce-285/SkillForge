package com.example.skillforge.feature.notifications.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillforge.core.notifications.NotificationHelper
import com.example.skillforge.domain.model.Notification
import com.example.skillforge.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val isNotificationLoading: Boolean = false,
    val errorMessage: String? = null,
)

class NotificationViewModel(
    private val notificationRepository: NotificationRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _notificationState = MutableStateFlow(NotificationUiState())
    val notificationState: StateFlow<NotificationUiState> = _notificationState.asStateFlow()

    private val shownNotificationIds = mutableSetOf<String>()

    fun fetchNotifications(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                _notificationState.value = _notificationState.value.copy(
                    isNotificationLoading = true,
                    errorMessage = null,
                )
            }

            try {
                notificationRepository.getNotifications()
                    .onSuccess { result ->
                        val newNotifications = result.notifications
                        
                        // Check for new unread notifications to show system notification
                        newNotifications.forEach { notification ->
                            if (!notification.isRead && !shownNotificationIds.contains(notification.id)) {
                                notificationHelper.showNotification(
                                    id = notification.id.hashCode(),
                                    title = notification.title,
                                    message = notification.message
                                )
                                shownNotificationIds.add(notification.id)
                            } else if (notification.isRead) {
                                shownNotificationIds.add(notification.id)
                            }
                        }

                        _notificationState.value = NotificationUiState(
                            notifications = newNotifications,
                            unreadCount = result.unreadCount,
                            isNotificationLoading = false,
                        )
                    }
                    .onFailure { error ->
                        Log.e("NotificationViewModel", "Failed to load notifications", error)
                        if (!silent) {
                            _notificationState.value = _notificationState.value.copy(
                                isNotificationLoading = false,
                                errorMessage = error.message ?: "Failed to load notifications",
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Failed to load notifications", e)
                if (!silent) {
                    _notificationState.value = _notificationState.value.copy(
                        isNotificationLoading = false,
                        errorMessage = e.message ?: "Failed to load notifications",
                    )
                }
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
                        Log.e("NotificationViewModel", "Failed to mark notification as read", error)
                        fetchNotifications(silent = true)
                    }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Failed to mark notification as read", e)
                fetchNotifications(silent = true)
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
                        Log.e("NotificationViewModel", "Failed to mark all notifications as read", error)
                        fetchNotifications(silent = true)
                    }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Failed to mark all notifications as read", e)
                fetchNotifications(silent = true)
            }
        }
    }
}

class NotificationViewModelFactory(
    private val notificationRepository: NotificationRepository,
    private val notificationHelper: NotificationHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(notificationRepository, notificationHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
