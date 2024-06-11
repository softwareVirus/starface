package com.starface.frontend

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starface.frontend.models.ActorResponse
import com.starface.frontend.repository.SearchRepository
import com.starface.frontend.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ActorViewModel@Inject constructor(private val searchRepository: SearchRepository): ViewModel() {
    val actorResponseLiveData: LiveData<NetworkResult<ActorResponse>>
        get() = searchRepository.actorResponseLiveData
    fun findActor(id: Int) {
        viewModelScope.launch {
            searchRepository.findActor(id)
        }
    }
    fun saveHistory(id: Int) {
        viewModelScope.launch {
            searchRepository.saveHistory(id)
        }
    }
}