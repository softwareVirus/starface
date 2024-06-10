package com.cheezycode.notesample.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cheezycode.notesample.api.UserAPI
import com.cheezycode.notesample.models.UserLoginRequest
import com.cheezycode.notesample.models.UserResponse
import com.cheezycode.notesample.models.UserSignupRequest
import com.cheezycode.notesample.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val userAPI: UserAPI) {
    private val _userReponseLiveData = MutableLiveData<NetworkResult<UserResponse>>()
    val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = _userReponseLiveData
    suspend fun registerUser(userRequest: UserSignupRequest) {
        _userReponseLiveData.postValue(NetworkResult.Loading())
        try{
            val response = userAPI.signup(userRequest)
            handleResponse(response)
        } catch(error: Exception) {
            _userReponseLiveData.postValue(NetworkResult.Error(error.message))
        }
    }

    suspend fun loginUser(userRequest: UserLoginRequest) {
        _userReponseLiveData.postValue(NetworkResult.Loading())
        try {
            val response = userAPI.signin(userRequest)
            handleResponse(response)
        } catch(error: Exception) {
            _userReponseLiveData.postValue(NetworkResult.Error(error.message))
        }
    }
    private fun handleResponse(response: Response<UserResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _userReponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _userReponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _userReponseLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }

}