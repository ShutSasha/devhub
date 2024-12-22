package com.devhub.devhubapp.api

import com.devhub.devhubapp.dataClasses.NotificationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.PATCH

interface NotificationAPI {
    @GET("notifications/{user_id}")
    fun getNotifications(@Path("user_id") userId: String): Call<NotificationResponse>

    @PATCH("notifications/{notification_id}")
    fun markAsRead(@Path("notification_id") notificationId: String): Call<Map<String, String>>
}