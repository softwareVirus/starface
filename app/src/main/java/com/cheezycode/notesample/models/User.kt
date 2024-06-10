package com.cheezycode.notesample.models

data class User(
    val userId: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val avatar: String,
    val gender: String
)