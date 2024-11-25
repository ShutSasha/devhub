package com.devhub.devhubapp.dataClasses;

data class AddCommentRequest(
    val postId: String,
    val content: String,
    val userId: String
)
