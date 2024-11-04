package com.devhub.devhubapp.dataClasses

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val user : User
)