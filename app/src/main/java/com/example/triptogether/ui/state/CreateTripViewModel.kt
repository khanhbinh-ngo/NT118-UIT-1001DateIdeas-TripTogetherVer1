package com.example.triptogether.ui.state

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triptogether.data.CreateTripDataSource
import com.example.triptogether.data.CreateTripRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateTripViewModel(private val repository: CreateTripDataSource = CreateTripRepository()) :
    ViewModel() {
    sealed class SaveState {
        data object Idle : SaveState()

        data object Saving : SaveState()

        data object Saved : SaveState()

        data class Failed(val message: Int, val detail: String? = null) : SaveState()
    }

    private val mutableState = MutableStateFlow<SaveState>(SaveState.Idle)
    val state: StateFlow<SaveState> = mutableState.asStateFlow()
    var image: Uri? = null

    fun save(name: String, country: String, start: String, end: String, emails: List<String>) {
        if (mutableState.value is SaveState.Saving) return
        mutableState.value = SaveState.Saving
        viewModelScope.launch {
            try {
                repository.create(name, country, start, end, emails, image)
                mutableState.value = SaveState.Saved
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                mutableState.value =
                    if (error is com.example.triptogether.data.UnregisteredTravelerException)
                        SaveState.Failed(
                            com.example.triptogether.R.string.unregistered_traveler,
                            error.email,
                        )
                    else if (error is com.example.triptogether.drive.DriveConnectionRequired)
                        SaveState.Failed(com.example.triptogether.R.string.drive_connection_failed)
                    else if (error.message?.contains("permission", ignoreCase = true) == true)
                        SaveState.Failed(
                            com.example.triptogether.R.string.database_permission_failed
                        )
                    else SaveState.Failed(com.example.triptogether.R.string.save_failed)
            }
        }
    }
}
