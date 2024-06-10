package com.cheezycode.notesample.api

import com.cheezycode.notesample.models.UserLoginRequest
import com.cheezycode.notesample.models.UserResponse
import com.cheezycode.notesample.models.UserSignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("/signup")
    suspend fun signup(@Body userSignupRequest: UserSignupRequest) : Response<UserResponse>

    @POST("/login")
    suspend fun signin(@Body userLoginRequest: UserLoginRequest) : Response<UserResponse>

}