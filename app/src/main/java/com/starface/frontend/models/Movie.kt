package com.starface.frontend.models
data class Movie(
    val movie_id: Int,
    val movie_name: String,
    val movie_detail: String,
    val imdb_rating: Double,
    val release_date: String,
    val img_url: String
)