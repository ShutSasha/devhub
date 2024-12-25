package com.devhub.devhubapp.dataClasses

import java.io.Serializable
import java.util.Date

data class Notification(
    val id: String,
    val reciever: String,
    val sender: Sender,
    val content: String,
    val createdAt: Date
) : Serializable