package com.devhub.devhubapp.dataClasses

data class UserRegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
    val repeatPassword: String
)