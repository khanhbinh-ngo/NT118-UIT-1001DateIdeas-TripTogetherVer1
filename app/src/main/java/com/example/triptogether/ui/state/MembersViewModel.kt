package com.example.triptogether.ui.state

import androidx.lifecycle.viewModelScope
import com.example.triptogether.data.TripDataSource
import com.example.triptogether.data.TripRepository
import com.example.triptogether.data.UserDataSource
import com.example.triptogether.data.UserRepository
import com.example.triptogether.model.Friend
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MembersViewModel(
    private val tripsRepository: TripDataSource = TripRepository(),
    private val usersRepository: UserDataSource = UserRepository(),
) : ActionViewModel() {
    private val mutableMembers = MutableStateFlow<UiState<List<Friend>>>(UiState.Loading)
    val members: StateFlow<UiState<List<Friend>>> = mutableMembers.asStateFlow()
    private var job: Job? = null

    fun load(creator: String, tripId: String) {
        job?.cancel()
        mutableMembers.value = UiState.Loading
        job =
            viewModelScope.launch {
                try {
                    val ids = tripsRepository.trip(creator, tripId)?.members.orEmpty()
                    val profiles = coroutineScope {
                        ids.map { uid -> async { usersRepository.profile(uid) } }.awaitAll()
                    }
                    mutableMembers.value =
                        UiState.Content(
                            profiles.map {
                                Friend(it.uid, it.name.ifBlank { it.email }, it.role, it.imageUrl)
                            }
                        )
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableMembers.value = UiState.Error
                }
            }
    }

    fun remove(creator: String, tripId: String, uid: String) = perform {
        tripsRepository.membership(creator, tripId, uid, false)
        load(creator, tripId)
    }

    fun invite(creator: String, tripId: String, email: String) = perform {
        val uid = checkNotNull(usersRepository.findUid(email)) { "Traveler not registered" }
        tripsRepository.membership(creator, tripId, uid, true)
        load(creator, tripId)
    }
}
