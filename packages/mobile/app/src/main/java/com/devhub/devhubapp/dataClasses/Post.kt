package com.devhub.devhubapp.dataClasses

data class Post(
    val _id: String,
    val headerImage: String?,
    var user: User,
    val title: String,
    val content: String,
    val createdAt: String,
    var likes: Int,
    var dislikes: Int,
    var saved: Int,
    val comments: List<Comment>?,
    val tags: List<String>?
)
