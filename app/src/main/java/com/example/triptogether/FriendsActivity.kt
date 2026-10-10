package com.example.triptogether

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triptogether.adapters.FriendsAdapter
import com.example.triptogether.data.AuthRepository
import com.example.triptogether.databinding.ActivityFriendsBinding
import com.example.triptogether.model.Trip
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.MembersViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe

class FriendsActivity : PlannerActivity() {
    private lateinit var model: MembersViewModel
    private var creator = ""
    private var tripId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tripId = intent.getStringExtra("tripId").orEmpty()
        creator = intent.getStringExtra("createdBy").orEmpty()
        if (tripId.isBlank() || creator.isBlank()) {
            finish()
            return
        }
        model = ViewModelProvider(this)[MembersViewModel::class.java]
        val uid = AuthRepository().currentUser?.uid
        val adapter = FriendsAdapter()
        binding.rvFriends.layoutManager = LinearLayoutManager(this)
        binding.rvFriends.adapter = adapter
        binding.btnHomeFriend.setOnClickListener { finish() }
        binding.btnEditFriends.setOnClickListener {
            startActivity(
                Intent(this, EditFriendsActivity::class.java)
                    .putExtra("trip", Trip(id = tripId, createdBy = creator))
            )
        }
        model.members.observe(this) { state ->
            when (state) {
                UiState.Loading -> showState(loading = true)
                UiState.Error ->
                    showState(
                        message = getString(R.string.load_error),
                        retry = { model.load(creator, tripId) },
                    )
                is UiState.Content -> {
                    val others = state.data.filter { it.uid != uid }
                    adapter.submitList(others)
                    showState(
                        message = if (others.isEmpty()) getString(R.string.no_members) else null
                    )
                    state.data
                        .firstOrNull { it.uid == uid }
                        ?.let {
                            binding.tvMainName.text = it.name
                            binding.tvMainRole.text = it.role
                            com.example.triptogether.drive.DriveImages.load(
                                binding.ivMainUser,
                                it.imageUrl,
                                R.drawable.ic_ui_user,
                                circle = true,
                            )
                        }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::model.isInitialized) model.load(creator, tripId)
    }
}
