package com.devhub.devhubapp

data class Errors(
    val Email: List<String>?,
    val Username: List<String>?,
    val Password: List<String>?,
    val RepeatPassword: List<String>?
)