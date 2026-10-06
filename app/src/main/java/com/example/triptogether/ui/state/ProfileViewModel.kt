package com.example.triptogether.ui.state

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.triptogether.data.AuthDataSource
import com.example.triptogether.data.AuthRepository
import com.example.triptogether.data.UserDataSource
import com.example.triptogether.data.UserRepository
import com.example.triptogether.model.UserProfile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: UserDataSource = UserRepository(),
    private val auth: AuthDataSource = AuthRepository(),
) : ActionViewModel() {
    private val mutableProfile = MutableStateFlow<UiState<UserProfile>>(UiState.Loading)
    val profile: StateFlow<UiState<UserProfile>> = mutableProfile.asStateFlow()
    var image: Uri? = null
    private var job: Job? = null

    fun load(uid: String? = auth.currentUser?.uid) {
        if (uid == null) {
            mutableProfile.value = UiState.Error
            return
        }
        job?.cancel()
        mutableProfile.value = UiState.Loading
        job =
            viewModelScope.launch {
                try {
                    mutableProfile.value = UiState.Content(repository.profile(uid))
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableProfile.value = UiState.Error
                }
            }
    }

    fun save(first: String, last: String, role: String) = perform {
        repository.save(first, last, role, image)
    }

    fun resetPassword() = perform { auth.resetPassword(checkNotNull(auth.currentUser?.email)) }
}
