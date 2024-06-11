package com.starface.frontend.models
data class Series(
    val series_id: Int,
    val series_name: String,
    val series_detail: String,
    val imdb_rating: Double,
    val starting_date: String,
    val finish_date: String,
    val season_number: Int,
    val img_url: String
)