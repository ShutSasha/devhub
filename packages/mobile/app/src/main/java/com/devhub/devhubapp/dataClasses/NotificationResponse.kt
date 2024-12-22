package com.devhub.devhubapp.dataClasses

data class NotificationResponse(
    val read: List<Notification>,
    val unread: List<Notification>
)