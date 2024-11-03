package com.devhub.devhubapp.dataClasses

data class ChangePasswordRequest(
    val email: String,
    val password: String,
    val repeatPassword: String
)
