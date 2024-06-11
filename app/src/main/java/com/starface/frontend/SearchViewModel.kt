package com.starface.frontend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starface.frontend.models.ActorSearchResponse
import com.starface.frontend.models.MovieSearchResponse
import com.starface.frontend.models.SeriesSearchResponse
import com.starface.frontend.models.UserResponse
import com.starface.frontend.repository.SearchRepository
import com.starface.frontend.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository) : ViewModel() {
    val actorSearchResponseLiveData: LiveData<NetworkResult<ActorSearchResponse>>
        get() = searchRepository.actorSearchResponseLiveData

    val movieSearchResponseLiveData: LiveData<NetworkResult<MovieSearchResponse>>
        get() = searchRepository.movieSearchResponseLiveData

    val seriesSearchResponseLiveData: LiveData<NetworkResult<SeriesSearchResponse>>
        get() = searchRepository.seriesSearchResponseLiveData
    val userSearchResponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = searchRepository.userResponseLiveData

    fun search(searchType: String, searchQuery: String) {
        Log.d("type",searchType)
        viewModelScope.launch {
            when (searchType) {
                "actor" -> searchRepository.searchActor(searchQuery)
                "movie" -> searchRepository.searchMovie(searchQuery)
                "series" -> searchRepository.searchSeries(searchQuery)
                else -> throw IllegalArgumentException("Unknown search type")
            }
        }
    }
    fun getUser() {
        viewModelScope.launch {
            searchRepository.getUser()
        }
    }
}
