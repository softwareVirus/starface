package com.starface.frontend.models

data class Actor(
    val actor_actress_id: Int,
    val firstname: String,
    val lastname: String,
    val age: Int,
    val gender: String,
    val biography: String,
    val img_url: String
)