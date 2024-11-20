package com.devhub.devhubapp.dataClasses

data class UserReactions(
    var likedPosts: List<String>? = emptyList(),
    var dislikedPosts: List<String>? = emptyList()
)