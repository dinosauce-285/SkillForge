package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.NotificationList

interface NotificationRepository {
    suspend fun getNotifications(
        page: Int = 1,
        limit: Int = 20,
        unreadOnly: Boolean? = null,
    ): Result<NotificationList>

    suspend fun markAsRead(notificationId: String): Result<Unit>

    suspend fun markAllAsRead(): Result<Unit>
}
