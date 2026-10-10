package com.example.triptogether.ui.state

import androidx.lifecycle.viewModelScope
import com.example.triptogether.data.TripDataSource
import com.example.triptogether.data.TripRepository
import com.example.triptogether.model.Trip
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TripsViewModel(private val repository: TripDataSource = TripRepository()) :
    ActionViewModel() {
    private val mutableTrips = MutableStateFlow<UiState<List<Trip>>>(UiState.Loading)
    val trips: StateFlow<UiState<List<Trip>>> = mutableTrips.asStateFlow()
    private var loadJob: Job? = null

    fun load(favorites: Boolean = false) {
        loadJob?.cancel()
        mutableTrips.value = UiState.Loading
        loadJob =
            viewModelScope.launch {
                try {
                    if (favorites)
                        repository.favorites().collect {
                            mutableTrips.value = UiState.Content(withUnread(it))
                        }
                    else mutableTrips.value = UiState.Content(withUnread(repository.loadTrips()))
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableTrips.value = UiState.Error
                }
            }
    }

    private suspend fun withUnread(trips: List<Trip>): List<Trip> = coroutineScope {
        trips
            .map { trip ->
                async {
                    val unread =
                        try {
                            repository.unread(trip.id)
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (_: Exception) {
                            false
                        }
                    trip.copy(hasUnreadMessages = unread)
                }
            }
            .awaitAll()
    }

    fun stop() {
        loadJob?.cancel()
    }

    fun toggleFavorite(trip: Trip, favorites: Boolean = false) = perform {
        repository.favorite(trip, !trip.isFavorite)
        load(favorites)
    }
}
