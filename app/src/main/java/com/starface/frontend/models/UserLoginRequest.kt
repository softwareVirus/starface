package com.starface.frontend.models

data class UserLoginRequest(
    val email: String,
    val password: String
)