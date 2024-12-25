package com.devhub.devhubapp.dataClasses

data class ReportRequest(
    val sender: String,
    val content: String,
    val category: String
)