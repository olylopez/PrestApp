package com.example.prestapp.data.remote.dtos

data class UserDto(
    val userID: Int = 0,
    val username: String,
    val email: String,
    val password: String
)