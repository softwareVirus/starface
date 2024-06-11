package com.starface.frontend.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.starface.frontend.api.SearchAPI
import com.starface.frontend.models.*
import com.starface.frontend.utils.NetworkResult
import retrofit2.Response
import javax.inject.Inject

class SearchRepository @Inject constructor(private val searchAPI: SearchAPI) {
    private val _actorSearchResponseLiveData = MutableLiveData<NetworkResult<ActorSearchResponse>>()
    val actorSearchResponseLiveData: LiveData<NetworkResult<ActorSearchResponse>>
        get() = _actorSearchResponseLiveData

    private val _movieSearchResponseLiveData = MutableLiveData<NetworkResult<MovieSearchResponse>>()
    val movieSearchResponseLiveData: LiveData<NetworkResult<MovieSearchResponse>>
        get() = _movieSearchResponseLiveData

    private val _seriesSearchResponseLiveData = MutableLiveData<NetworkResult<SeriesSearchResponse>>()
    val seriesSearchResponseLiveData: LiveData<NetworkResult<SeriesSearchResponse>>
        get() = _seriesSearchResponseLiveData
    private val _actorResponseLiveData = MutableLiveData<NetworkResult<ActorResponse>>()
    val actorResponseLiveData: LiveData<NetworkResult<ActorResponse>>
        get() = _actorResponseLiveData
    private val _historyResponseLiveData = MutableLiveData<NetworkResult<HistoryResponse>>()
    val historyResponseLiveData: LiveData<NetworkResult<HistoryResponse>>
        get() = _historyResponseLiveData
    private val _userResponseLiveData = MutableLiveData<NetworkResult<UserResponse>>()
    val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = _userResponseLiveData

    suspend fun searchActor(searchString: String) {
        _actorSearchResponseLiveData.postValue(NetworkResult.Loading())
        val response = searchAPI.searchActor(searchString)
        handleResponse<ActorSearchResponse>(response, _actorSearchResponseLiveData)
    }

    suspend fun searchMovie(searchString: String) {
        _movieSearchResponseLiveData.postValue(NetworkResult.Loading())
        val response = searchAPI.searchMovie(searchString)
        handleResponse<MovieSearchResponse>(response, _movieSearchResponseLiveData)
    }

    suspend fun searchSeries(searchString: String) {
        _seriesSearchResponseLiveData.postValue(NetworkResult.Loading())
        val response = searchAPI.searchSeries(searchString)
        handleResponse<SeriesSearchResponse>(response, _seriesSearchResponseLiveData)
    }
    suspend fun findActor(id: Int) {
        _actorResponseLiveData.postValue(NetworkResult.Loading())
        val response = searchAPI.findActor(id)
        handleResponse<ActorResponse>(response, _actorResponseLiveData)
    }
    suspend fun saveHistory(id: Int) {
        try {
            searchAPI.saveHistory(HistoryRequest(id))
        } catch (error: Exception) {

        }
    }
    suspend fun getHistory(page: Int,limit: Int) {
        _historyResponseLiveData.postValue(NetworkResult.Loading())
        val response = searchAPI.getHistory(page, limit)
        handleResponse<HistoryResponse>(response, _historyResponseLiveData)
    }
    suspend fun getUser() {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response = searchAPI.getUser()
        handleResponse<UserResponse>(response, _userResponseLiveData)
    }
    private fun <T> handleResponse(response: Response<T>, liveData: MutableLiveData<NetworkResult<T>>) {
        try {
            if (response.isSuccessful) {
                liveData.postValue(NetworkResult.Success(response.body()!!))
            } else {
                liveData.postValue(NetworkResult.Error(response.message()))
            }
        } catch (error: Exception) {
            liveData.postValue(NetworkResult.Error(error.message ?: "Unknown error"))
        }
    }
}
