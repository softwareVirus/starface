package com.cheezycode.notesample

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheezycode.notesample.models.ActorResponse
import com.cheezycode.notesample.models.UserResponse
import com.cheezycode.notesample.models.UserSignupRequest
import com.cheezycode.notesample.repository.SearchRepository
import com.cheezycode.notesample.repository.UserRepository
import com.cheezycode.notesample.utils.NetworkResult
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