package com.cheezycode.notesample

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheezycode.notesample.models.HistoryResponse
import com.cheezycode.notesample.repository.SearchRepository
import com.cheezycode.notesample.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val historyRepository: SearchRepository) : ViewModel() {
    val historyResponseLiveData: LiveData<NetworkResult<HistoryResponse>>
        get() = historyRepository.historyResponseLiveData

    fun getHistory(page: Int, limit: Int) {
        viewModelScope.launch {
            historyRepository.getHistory(page, limit)
        }
    }
}
