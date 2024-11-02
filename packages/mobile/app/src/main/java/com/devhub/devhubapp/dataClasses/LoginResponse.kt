package com.devhub.devhubapp.dataClasses

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
)

data class UserResponse(
    val id: String,
    val name: String?,
    val userName: String,
    val avatar: String,
    val email: String,
    val createdAt: String,
    val devPoints: Int,
    val activationCode: String,
    val isActivated: Boolean,
    val userRole: Array<String>
)