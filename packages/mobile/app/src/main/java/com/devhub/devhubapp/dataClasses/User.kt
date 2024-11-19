package com.devhub.devhubapp.dataClasses

import java.util.Date

data class User(
    val _id: String,
    val name: String?,
    val username: String,
    val avatar: String,
    val email: String,
    val createdAt: Date,
    val devPoints: Int,
    val activationCode: String,
    val isActivated: Boolean,
    val roles: Array<String>
) {
}