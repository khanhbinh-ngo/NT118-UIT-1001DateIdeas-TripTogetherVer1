package com.example.triptogether.ui.state

import androidx.lifecycle.viewModelScope
import com.example.triptogether.data.PlanDataSource
import com.example.triptogether.data.PlanRepository
import com.example.triptogether.model.ActivityItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlanViewModel(private val repository: PlanDataSource = PlanRepository()) : ActionViewModel() {
    private val mutableItems =
        MutableStateFlow<UiState<List<Pair<ActivityItem, String>>>>(UiState.Loading)
    val items: StateFlow<UiState<List<Pair<ActivityItem, String>>>> = mutableItems.asStateFlow()
    private var job: Job? = null

    fun load(creator: String, tripId: String, index: Int) {
        job?.cancel()
        mutableItems.value = UiState.Loading
        job =
            viewModelScope.launch {
                try {
                    mutableItems.value =
                        UiState.Content(repository.activities(creator, tripId, index))
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableItems.value = UiState.Error
                }
            }
    }

    fun save(creator: String, tripId: String, index: Int, key: String?, item: ActivityItem) =
        perform {
            repository.save(creator, tripId, index, key, item)
        }

    fun delete(creator: String, tripId: String, index: Int, key: String) = perform {
        repository.delete(creator, tripId, index, key)
    }
}
