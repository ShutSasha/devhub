package com.devhub.devhubapp

data class UserRegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
    val repeatPassword: String
)
