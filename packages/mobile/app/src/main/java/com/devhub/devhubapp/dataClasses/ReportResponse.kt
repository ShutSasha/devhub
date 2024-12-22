package com.devhub.devhubapp.dataClasses

data class ReportResponse(
    val _id: String,
    val sender: String,
    val content: String,
    val category: String
)