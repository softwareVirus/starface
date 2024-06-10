package com.cheezycode.notesample.models

data class ActorResponse(
    val actor: Actor,
    val movies: List<Movie>,
    val series: List<Series>
)
