package com.devhub.devhubapp.dataClasses

data class UserResponse(
    val id: String,
    val name: String?,
    val username: String,
    val avatar: String,
    val email: String,
    val createdAt: String,
    val devPoints: Int,
    val activationCode: String,
    val isActivated: Boolean,
    val userRole: Array<String>
)