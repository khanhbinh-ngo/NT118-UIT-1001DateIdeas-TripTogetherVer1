package com.example.triptogether

import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triptogether.adapters.EditFriendsAdapter
import com.example.triptogether.databinding.ActivityEditFriendsBinding
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.ActionState
import com.example.triptogether.ui.state.MembersViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe
import com.example.triptogether.utilities.tripExtra
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditFriendsActivity : PlannerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val trip =
            intent.tripExtra()
                ?: run {
                    finish()
                    return
                }
        val model = ViewModelProvider(this)[MembersViewModel::class.java]
        val adapter =
            EditFriendsAdapter(currentUserId = trip.createdBy) { friend ->
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.remove_traveler)
                    .setMessage(getString(R.string.remove_traveler_confirm, friend.name))
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.remove) { _, _ ->
                        model.remove(trip.createdBy, trip.id, friend.uid)
                    }
                    .show()
            }
        binding.rvEditFriends.layoutManager = LinearLayoutManager(this)
        binding.rvEditFriends.adapter = adapter
        model.members.observe(this) { state ->
            when (state) {
                UiState.Loading -> showState(loading = true)
                UiState.Error ->
                    showState(
                        message = getString(R.string.load_error),
                        retry = { model.load(trip.createdBy, trip.id) },
                    )
                is UiState.Content -> {
                    adapter.submitList(state.data)
                    showState(
                        message = if (state.data.isEmpty()) getString(R.string.no_members) else null
                    )
                }
            }
        }
        model.action.observe(this) { state ->
            binding.btnAddFriend.isEnabled = state != ActionState.Running
            if (state == ActionState.Error) {
                toast(R.string.member_update_failed)
                model.acknowledge()
            } else if (state == ActionState.Done) model.acknowledge()
        }
        binding.btnAddFriend.setOnClickListener {
            val input =
                EditText(this).apply {
                    hint = getString(R.string.email)
                    inputType =
                        android.text.InputType.TYPE_CLASS_TEXT or
                            android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                }
            val dialog =
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.add_traveler)
                    .setView(input)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.add, null)
                    .create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val email = input.text.toString().trim()
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                        input.error = getString(R.string.invalid_email)
                    else {
                        model.invite(trip.createdBy, trip.id, email)
                        dialog.dismiss()
                    }
                }
            }
            dialog.show()
        }
        model.load(trip.createdBy, trip.id)
    }
}
