package com.example.triptogether.ui.state

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>

    data class Content<T>(val data: T) : UiState<T>

    data object Error : UiState<Nothing>
}

sealed interface ActionState {
    data object Idle : ActionState

    data object Running : ActionState

    data object Done : ActionState

    data object Error : ActionState
}
