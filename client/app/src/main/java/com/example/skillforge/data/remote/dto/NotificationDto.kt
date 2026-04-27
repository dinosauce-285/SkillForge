package com.example.skillforge.data.remote.dto

data class NotificationDto(
    val id: String,
    val recipientId: String,
    val actorId: String?,
    val type: String,
    val title: String,
    val message: String,
    val metadata: Map<String, Any?>?,
    val readAt: String?,
    val createdAt: String,
    val actor: NotificationActorDto?,
)

data class NotificationActorDto(
    val id: String,
    val fullName: String,
    val profile: NotificationActorProfileDto?,
)

data class NotificationActorProfileDto(
    val avatarUrl: String?,
)

data class NotificationListResponseDto(
    val data: List<NotificationDto>,
    val meta: NotificationMetaDto,
)

data class NotificationMetaDto(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int,
    val unreadCount: Int,
)
