package com.devhub.devhubapp.dataClasses

data class VerifyEmailRequest(
    val email: String,
    val activationCode: String
)
