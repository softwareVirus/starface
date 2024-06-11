package com.starface.frontend.api

import com.starface.frontend.models.Actor
import com.starface.frontend.models.ActorResponse
import com.starface.frontend.models.ActorSearchResponse
import com.starface.frontend.models.HistoryRequest
import com.starface.frontend.models.HistoryResponse
import com.starface.frontend.models.MovieSearchResponse
import com.starface.frontend.models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import com.starface.frontend.models.SeriesSearchResponse
import retrofit2.http.POST

interface SearchAPI {
    @GET("/search/actor/{searchString}")
    suspend fun searchActor(@Path("searchString") searchString: String): Response<ActorSearchResponse>

    @GET("/search/movie/{searchString}")
    suspend fun searchMovie(@Path("searchString") searchString: String): Response<MovieSearchResponse>

    @GET("/search/series/{searchString}")
    suspend fun searchSeries(@Path("searchString") searchString: String): Response<SeriesSearchResponse>

    @GET("/actor/{id}")
    suspend fun findActor(@Path("id") id: Int): Response<ActorResponse>

    @POST("/history")
    suspend fun saveHistory(@Body historyRequest: HistoryRequest): Response<Actor>
    @GET("/history/{page}/{limit}")
    suspend fun getHistory(@Path("page") page: Int, @Path("limit") limit: Int): Response<HistoryResponse>

    @GET("/user")
    suspend fun getUser(): Response<UserResponse>
}

