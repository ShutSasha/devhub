package com.devhub.devhubapp.dataClasses

import java.util.Date

data class UserDetail(
    val _id: String,
    val bio: String? = null,
    val avatar: String,
    val name: String? = null,
    val username: String,
    val createdAt: Date,
    val posts: List<Post>? = null,
    val comments: List<Comment>? = null
) {
}