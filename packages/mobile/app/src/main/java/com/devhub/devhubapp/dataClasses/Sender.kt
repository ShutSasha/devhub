package com.devhub.devhubapp.dataClasses

import java.io.Serializable

data class Sender(
    val id: String,
    val username: String,
    val avatar: String
) : Serializable