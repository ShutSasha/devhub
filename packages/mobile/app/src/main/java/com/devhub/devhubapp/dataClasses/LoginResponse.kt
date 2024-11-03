package com.devhub.devhubapp.dataClasses

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
)
