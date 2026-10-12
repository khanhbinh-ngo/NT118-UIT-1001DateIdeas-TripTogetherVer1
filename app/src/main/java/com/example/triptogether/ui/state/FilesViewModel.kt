package com.example.triptogether.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triptogether.data.FilesDataSource
import com.example.triptogether.data.FilesRepository
import com.example.triptogether.model.SharedItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FilesViewModel(private val repository: FilesDataSource = FilesRepository()) : ViewModel() {
    private val mutableItems = MutableStateFlow<UiState<List<SharedItem>>>(UiState.Loading)
    val items: StateFlow<UiState<List<SharedItem>>> = mutableItems.asStateFlow()
    private var job: Job? = null
    var photos = false

    fun load(tripId: String, photos: Boolean = this.photos) {
        this.photos = photos
        job?.cancel()
        mutableItems.value = UiState.Loading
        job =
            viewModelScope.launch {
                try {
                    repository.items(tripId, photos).collect {
                        mutableItems.value = UiState.Content(it)
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableItems.value = UiState.Error
                }
            }
    }

    fun stop() {
        job?.cancel()
    }
}
