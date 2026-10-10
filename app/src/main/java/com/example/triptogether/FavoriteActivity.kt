package com.example.triptogether

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triptogether.adapters.TripAdapter
import com.example.triptogether.databinding.ActivityFavoritesBinding
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.TripsViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe

class FavoriteActivity : PlannerActivity() {
    private lateinit var model: TripsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        model = ViewModelProvider(this)[TripsViewModel::class.java]
        val adapter =
            TripAdapter(
                compact = true,
                onOpen = {
                    startActivity(
                        Intent(this, TripSummaryActivity::class.java).putExtra("trip", it)
                    )
                },
                onFavorite = { model.toggleFavorite(it, favorites = true) },
            )
        binding.recyclerFavorites.layoutManager = LinearLayoutManager(this)
        binding.recyclerFavorites.adapter = adapter
        model.trips.observe(this) { state ->
            when (state) {
                UiState.Loading -> showState(loading = true)
                UiState.Error ->
                    showState(
                        message = getString(R.string.load_error),
                        retry = { model.load(true) },
                    )
                is UiState.Content -> {
                    adapter.submitList(state.data)
                    showState(
                        message =
                            if (state.data.isEmpty()) getString(R.string.favorites_empty_hint)
                            else null
                    )
                }
            }
        }
        observeAction(model)
    }

    override fun onStart() {
        super.onStart()
        model.load(true)
    }

    override fun onStop() {
        model.stop()
        super.onStop()
    }
}
