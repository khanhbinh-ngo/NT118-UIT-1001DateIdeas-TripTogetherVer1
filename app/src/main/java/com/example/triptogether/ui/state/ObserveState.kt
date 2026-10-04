package com.example.triptogether.ui.state

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Collect only while the screen is started; automatically cancel with its lifecycle. */
fun <T> StateFlow<T>.observe(owner: LifecycleOwner, render: (T) -> Unit) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(Lifecycle.State.STARTED) { collect { render(it) } }
    }
}
