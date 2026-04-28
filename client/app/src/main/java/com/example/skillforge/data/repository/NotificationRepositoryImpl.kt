package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.NotificationApi
import com.example.skillforge.data.remote.dto.NotificationDto
import com.example.skillforge.domain.model.Notification
import com.example.skillforge.domain.model.NotificationList
import com.example.skillforge.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val api: NotificationApi,
) : NotificationRepository {

    override suspend fun getNotifications(
        page: Int,
        limit: Int,
        unreadOnly: Boolean?,
    ): Result<NotificationList> {
        return try {
            val response = api.getNotifications(page, limit, unreadOnly)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                Result.success(
                    NotificationList(
                        notifications = body.data.map { it.toDomain() },
                        unreadCount = body.meta.unreadCount,
                    ),
                )
            } else {
                Result.failure(Exception("Failed to load notifications"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to load notifications"))
        }
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            val response = api.markAsRead(notificationId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark notification as read"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to mark notification as read"))
        }
    }

    override suspend fun markAllAsRead(): Result<Unit> {
        return try {
            val response = api.markAllAsRead()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark notifications as read"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to mark notifications as read"))
        }
    }

    private fun NotificationDto.toDomain(): Notification {
        return Notification(
            id = id,
            type = type,
            title = title,
            message = message,
            metadata = metadata,
            readAt = readAt,
            createdAt = createdAt,
        )
    }
}
