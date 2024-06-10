package com.cheezycode.notesample.api

import com.cheezycode.notesample.models.Actor
import com.cheezycode.notesample.models.ActorResponse
import com.cheezycode.notesample.models.ActorSearchResponse
import com.cheezycode.notesample.models.HistoryRequest
import com.cheezycode.notesample.models.HistoryResponse
import com.cheezycode.notesample.models.MovieSearchResponse
import com.cheezycode.notesample.models.UserLoginRequest
import com.cheezycode.notesample.models.UserResponse
import com.cheezycode.notesample.models.UserSignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call
import com.cheezycode.notesample.models.SeriesSearchResponse
import com.cheezycode.notesample.models.User
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

