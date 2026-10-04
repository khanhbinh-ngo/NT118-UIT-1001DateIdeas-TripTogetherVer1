package com.example.triptogether.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Retains a write operation through recreation without retaining UI references. */
open class ActionViewModel : ViewModel() {
    private val mutableAction = MutableStateFlow<ActionState>(ActionState.Idle)
    val action: StateFlow<ActionState> = mutableAction.asStateFlow()

    protected fun perform(block: suspend () -> Unit) {
        if (mutableAction.value == ActionState.Running) return
        mutableAction.value = ActionState.Running
        viewModelScope.launch {
            try {
                block()
                mutableAction.value = ActionState.Done
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableAction.value = ActionState.Error
            }
        }
    }

    fun acknowledge() {
        if (mutableAction.value != ActionState.Running) mutableAction.value = ActionState.Idle
    }
}
