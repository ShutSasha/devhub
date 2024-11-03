package com.devhub.devhubapp.dataClasses

data class ErrorResponse(
    val type: String?,
    val title: String?,
    val status: Int?,
    val errors: Errors?
)