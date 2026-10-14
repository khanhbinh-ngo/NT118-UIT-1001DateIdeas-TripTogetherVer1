package com.example.triptogether.ui.state

import androidx.lifecycle.viewModelScope
import com.example.triptogether.data.ChatRepository
import com.example.triptogether.data.TripRepository
import com.example.triptogether.model.Trip
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationsViewModel : ActionViewModel() {
    private val trips = TripRepository()
    private val chats = ChatRepository()
    private val mutableItems = MutableStateFlow<UiState<List<Trip>>>(UiState.Loading)
    val items = mutableItems.asStateFlow()
    private var job: Job? = null

    fun load() {
        job?.cancel()
        mutableItems.value = UiState.Loading
        job =
            viewModelScope.launch {
                try {
                    mutableItems.value =
                        UiState.Content(trips.loadTrips().filter { trips.unread(it.id) })
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableItems.value = UiState.Error
                }
            }
    }

    fun markAllRead() = perform {
        val current = (mutableItems.value as? UiState.Content)?.data.orEmpty()
        current.forEach { trip ->
            chats
                .messages(trip.id)
                .first()
                .maxOfOrNull { it.timestamp }
                ?.let { chats.markSeen(trip.id, it) }
        }
        load()
    }
}
