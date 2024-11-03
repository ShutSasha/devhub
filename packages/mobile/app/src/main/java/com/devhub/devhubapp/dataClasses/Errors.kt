package com.devhub.devhubapp.dataClasses

import com.google.gson.annotations.SerializedName

data class Errors(
    val Email: List<String>?,
    val Username: List<String>?,
    val Password: List<String>?,
    val RepeatPassword: List<String>?,
    @SerializedName("Login error")
    val LoginError: List<String>?,
    @SerializedName("Registration error")
    val RegistrationError: List<String>?,
    @SerializedName("Send Verification error")
    val VerificationError: List<String>?,
    val ActivationCode: List<String>?
)