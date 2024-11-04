package com.devhub.devhubapp.dataClasses

import com.google.gson.annotations.SerializedName
import java.util.Date


data class Post(
    val _id: String,
    val headerImage: String?,
    val user: User,
    val title: String,
    val content: String,
    val createdAt: String,
    val likes: Int,
    val dislikes: Int,
    val comments: List<Comment>,
    val tags: List<String>?
)
