package com.devhub.devhubapp.dataClasses

data class SavedPostDetailsResponse(
    val _id: String,
    val title: String,
    val user: User,
    val content: String,
    val headerImage: String?,
    val createdAt: String,
    val likes: Int,
    val dislikes: Int,
    val saved: Int,
    val tags: List<String>?,
    val comments: List<String>?
)