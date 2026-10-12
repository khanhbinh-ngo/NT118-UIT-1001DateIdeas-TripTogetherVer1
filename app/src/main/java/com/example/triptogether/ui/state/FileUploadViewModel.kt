package com.example.triptogether.ui.state

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triptogether.R
import com.example.triptogether.data.FilesDataSource
import com.example.triptogether.data.FilesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FileUploadViewModel(private val repository: FilesDataSource = FilesRepository()) :
    ViewModel() {
    sealed class State {
        data object Idle : State()

        data class Uploading(val percent: Int) : State()

        data class Saved(val image: Boolean) : State()

        data class Failed(val message: Int) : State()
    }

    private val mutableState = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = mutableState.asStateFlow()

    fun acknowledge() {
        if (mutableState.value !is State.Uploading) mutableState.value = State.Idle
    }

    fun upload(uri: Uri, mime: String, extension: String, creator: String, tripId: String) {
        if (mutableState.value is State.Uploading) return
        mutableState.value = State.Uploading(0)
        viewModelScope.launch {
            try {
                val image =
                    repository.upload(uri, mime, extension, creator, tripId) {
                        mutableState.value = State.Uploading(it)
                    }
                mutableState.value = State.Saved(image)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                mutableState.value =
                    State.Failed(
                        if (error is com.example.triptogether.drive.DriveConnectionRequired)
                            R.string.drive_connection_failed
                        else if (error is com.example.triptogether.drive.DrivePrivacyException)
                            R.string.drive_restricted_required
                        else R.string.upload_failed
                    )
            }
        }
    }
}
