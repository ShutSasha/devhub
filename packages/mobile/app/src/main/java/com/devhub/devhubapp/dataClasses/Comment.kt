package com.devhub.devhubapp.dataClasses

data class Comment(
    val _id: String,
    val commentText: String,
    val createdAt: String,
    val likes: Int,
    val user: String
)