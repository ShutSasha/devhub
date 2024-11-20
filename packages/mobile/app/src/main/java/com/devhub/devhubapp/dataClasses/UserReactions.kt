package com.devhub.devhubapp.dataClasses

data class UserReactions(
    val likedPosts: List<String>? = emptyList(),
    val dislikedPosts: List<String>? = emptyList()
)