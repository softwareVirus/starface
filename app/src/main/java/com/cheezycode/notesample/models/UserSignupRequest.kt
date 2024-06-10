package com.cheezycode.notesample.models

data class UserSignupRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
    val avatar: String,
    val gender: String,
)