package com.example.triptogether.ui.state

import androidx.lifecycle.viewModelScope
import com.example.triptogether.data.TripDataSource
import com.example.triptogether.data.TripRepository
import com.example.triptogether.data.UserDataSource
import com.example.triptogether.data.UserRepository
import com.example.triptogether.model.Trip
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TripDetails(val trip: Trip, val creatorEmail: String, val favorite: Boolean)

class TripDetailViewModel(
    private val repository: TripDataSource = TripRepository(),
    private val usersRepository: UserDataSource = UserRepository(),
) : ActionViewModel() {
    private val mutableDetails = MutableStateFlow<UiState<TripDetails>>(UiState.Loading)
    val details: StateFlow<UiState<TripDetails>> = mutableDetails.asStateFlow()
    private var job: Job? = null

    fun load(trip: Trip) {
        job?.cancel()
        mutableDetails.value = UiState.Loading
        job =
            viewModelScope.launch {
                try {
                    val current = repository.trip(trip.createdBy, trip.id) ?: trip
                    mutableDetails.value =
                        UiState.Content(
                            TripDetails(
                                current,
                                usersRepository.profile(trip.createdBy).email,
                                repository.isFavorite(trip.id),
                            )
                        )
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableDetails.value = UiState.Error
                }
            }
    }

    fun toggle(trip: Trip, favorite: Boolean) = perform {
        repository.favorite(trip, !favorite)
        load(trip)
    }
}
