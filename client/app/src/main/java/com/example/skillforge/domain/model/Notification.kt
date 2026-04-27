package com.example.skillforge.domain.model

data class Notification(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val metadata: Map<String, Any?>?,
    val readAt: String?,
    val createdAt: String,
) {
    val isRead: Boolean
        get() = readAt != null
}

data class NotificationList(
    val notifications: List<Notification>,
    val unreadCount: Int,
)
