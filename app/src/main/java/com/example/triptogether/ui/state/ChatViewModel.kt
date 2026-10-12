package com.example.triptogether.ui.state

import androidx.lifecycle.viewModelScope
import com.example.triptogether.data.ChatDataSource
import com.example.triptogether.data.ChatRepository
import com.example.triptogether.model.ChatMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatDataSource = ChatRepository()) : ActionViewModel() {
    private val mutableMessages = MutableStateFlow<UiState<List<ChatMessage>>>(UiState.Loading)
    val messages: StateFlow<UiState<List<ChatMessage>>> = mutableMessages.asStateFlow()
    private var job: Job? = null
    var sentText = ""
        private set

    fun load(tripId: String) {
        job?.cancel()
        mutableMessages.value = UiState.Loading
        job =
            viewModelScope.launch {
                try {
                    repository.messages(tripId).collect { messages ->
                        mutableMessages.value = UiState.Content(messages)
                        val latest = messages.maxOfOrNull(ChatMessage::timestamp)
                        if (latest != null) {
                            try {
                                repository.markSeen(tripId, latest)
                            } catch (cancelled: CancellationException) {
                                throw cancelled
                            } catch (_: Exception) {
                                /* Reading chat remains available when the read marker cannot be written. */
                            }
                        }
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableMessages.value = UiState.Error
                }
            }
    }

    fun stop() {
        job?.cancel()
    }

    fun send(tripId: String, text: String) = perform {
        repository.send(tripId, text)
        sentText = text
    }
}
