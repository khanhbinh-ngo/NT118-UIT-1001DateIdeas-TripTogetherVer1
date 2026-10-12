package com.example.triptogether

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triptogether.adapters.ChatMessageAdapter
import com.example.triptogether.data.AuthRepository
import com.example.triptogether.databinding.ActivityGroupChatBinding
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.ActionState
import com.example.triptogether.ui.state.ChatViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe

class GroupChatActivity : PlannerActivity() {
    private lateinit var model: ChatViewModel
    private var tripId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tripId = intent.getStringExtra("tripId").orEmpty()
        if (tripId.isBlank()) {
            finish()
            return
        }
        model = ViewModelProvider(this)[ChatViewModel::class.java]
        val adapter =
            ChatMessageAdapter(
                currentEmail = AuthRepository().currentUser?.email,
                currentUid = AuthRepository().currentUser?.uid,
            )
        binding.rvMessages.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        binding.rvMessages.adapter = adapter
        binding.tvChatTitle.text =
            intent.getStringExtra("tripName") ?: getString(R.string.group_chat)
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotBlank()) model.send(tripId, text)
        }
        model.messages.observe(this) { state ->
            when (state) {
                UiState.Loading -> showState(loading = true)
                UiState.Error ->
                    showState(
                        message = getString(R.string.load_error),
                        retry = { model.load(tripId) },
                    )
                is UiState.Content -> {
                    adapter.submitList(state.data) {
                        if (adapter.itemCount > 0)
                            binding.rvMessages.scrollToPosition(adapter.itemCount - 1)
                    }
                    showState(
                        message =
                            if (state.data.isEmpty()) getString(R.string.no_messages) else null
                    )
                }
            }
        }
        model.action.observe(this) { state ->
            binding.btnSend.isEnabled = state != ActionState.Running
            if (state == ActionState.Done) {
                if (binding.etMessage.text.toString().trim() == model.sentText)
                    binding.etMessage.text?.clear()
                model.acknowledge()
            } else if (state == ActionState.Error) {
                toast(R.string.save_failed)
                model.acknowledge()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (::model.isInitialized) model.load(tripId)
    }

    override fun onStop() {
        if (::model.isInitialized) model.stop()
        super.onStop()
    }
}
