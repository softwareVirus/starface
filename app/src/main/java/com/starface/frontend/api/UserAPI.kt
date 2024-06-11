package com.starface.frontend.api

import com.starface.frontend.models.UserLoginRequest
import com.starface.frontend.models.UserResponse
import com.starface.frontend.models.UserSignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("/signup")
    suspend fun signup(@Body userSignupRequest: UserSignupRequest) : Response<UserResponse>

    @POST("/login")
    suspend fun signin(@Body userLoginRequest: UserLoginRequest) : Response<UserResponse>

}