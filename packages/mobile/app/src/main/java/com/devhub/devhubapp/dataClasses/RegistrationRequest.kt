package com.devhub.devhubapp.dataClasses

data class RegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
    val repeatPassword: String
)