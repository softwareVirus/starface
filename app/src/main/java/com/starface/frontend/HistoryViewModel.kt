package com.starface.frontend

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starface.frontend.models.HistoryResponse
import com.starface.frontend.repository.SearchRepository
import com.starface.frontend.utils.NetworkResult
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
