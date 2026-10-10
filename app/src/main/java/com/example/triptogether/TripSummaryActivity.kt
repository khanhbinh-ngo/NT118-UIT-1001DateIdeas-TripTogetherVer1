package com.example.triptogether

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.triptogether.databinding.ActivityTripSummaryBinding
import com.example.triptogether.model.Trip
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.ActionState
import com.example.triptogether.ui.state.TripDetailViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe
import com.example.triptogether.utilities.tripExtra

class TripSummaryActivity : PlannerActivity() {
    private lateinit var binding: ActivityTripSummaryBinding
    private lateinit var model: TripDetailViewModel
    private lateinit var trip: Trip
    private var favorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        trip =
            intent.tripExtra()
                ?: run {
                    finish()
                    return
                }
        model = ViewModelProvider(this)[TripDetailViewModel::class.java]
        render()
        binding.btnBack.setOnClickListener { finish() }
        binding.btnEnterPlan.setOnClickListener {
            startActivity(Intent(this, TripPlanActivity::class.java).putExtra("trip", trip))
        }
        binding.btnFavorite.setOnClickListener { model.toggle(trip, favorite) }
        binding.btnChat.setOnClickListener {
            startActivity(
                Intent(this, GroupChatActivity::class.java)
                    .putExtra("tripId", trip.id)
                    .putExtra("tripName", trip.name)
            )
        }
        binding.btnDocuments.setOnClickListener {
            startActivity(
                Intent(this, DocumentsHomeActivity::class.java)
                    .putExtra("tripId", trip.id)
                    .putExtra("createdBy", trip.createdBy)
            )
        }
        binding.icUsers.setOnClickListener {
            startActivity(
                Intent(this, FriendsActivity::class.java)
                    .putExtra("tripId", trip.id)
                    .putExtra("createdBy", trip.createdBy)
            )
        }
        binding.tvMembers.setOnClickListener { binding.icUsers.performClick() }
        binding.mapTab.setOnClickListener {
            startActivity(
                Intent(this, TripMapActivity::class.java)
                    .putExtra("tripLat", trip.latitude)
                    .putExtra("tripLng", trip.longitude)
                    .putExtra("country", trip.country)
            )
        }
        model.details.observe(this) { state ->
            when (state) {
                is UiState.Content -> {
                    trip = state.data.trip
                    favorite = state.data.favorite
                    binding.tvDescription.text =
                        getString(R.string.created_by, state.data.creatorEmail)
                    render()
                }
                UiState.Error -> toast(R.string.load_error)
                UiState.Loading -> Unit
            }
        }
        model.action.observe(this) { binding.btnFavorite.isEnabled = it != ActionState.Running }
        observeAction(model)
    }

    private fun render() {
        binding.tvTheTripName.text = trip.name
        binding.tvCountry.text = trip.country
        binding.tvDates.text = getString(R.string.trip_dates, trip.startDate, trip.endDate)
        binding.tvMembers.text = getString(R.string.members_count, trip.members.size)
        binding.btnFavorite.setImageResource(
            if (favorite) R.drawable.ic_ui_heart_filled else R.drawable.ic_ui_heart
        )
        binding.btnFavorite.contentDescription =
            getString(if (favorite) R.string.remove_favorite else R.string.add_favorite)
        com.example.triptogether.drive.DriveImages.load(
            binding.ivTripImage,
            trip.imageUrl,
            R.drawable.default_trip_image,
            circle = false,
        )
    }

    override fun onResume() {
        super.onResume()
        if (::trip.isInitialized) model.load(trip)
    }
}
