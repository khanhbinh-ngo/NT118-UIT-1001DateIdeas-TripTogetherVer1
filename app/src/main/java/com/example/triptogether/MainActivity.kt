package com.example.triptogether

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triptogether.adapters.TripAdapter
import com.example.triptogether.databinding.ActivityMainBinding
import com.example.triptogether.model.Trip
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.ProfileViewModel
import com.example.triptogether.ui.state.TripsViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe
import com.example.triptogether.utilities.TripFilters

open class MainActivity : PlannerActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: TripsViewModel
    private lateinit var adapter: TripAdapter
    private var trips = emptyList<Trip>()
    private var tab = "upcoming"
    protected open val allTripsScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        model = ViewModelProvider(this)[TripsViewModel::class.java]
        tab = savedInstanceState?.getString("tab") ?: if (allTripsScreen) "all" else "upcoming"
        binding.btnAll.visibility = if (allTripsScreen) View.VISIBLE else View.GONE
        if (allTripsScreen) {
            binding.homeTitle.visibility = View.VISIBLE
            binding.homeTitle.setText(R.string.my_trips)
            binding.greetingText.setText(R.string.my_trips_hint)
        }
        binding.greetingText.visibility = View.GONE
        binding.homeTitle.visibility = if (allTripsScreen) View.VISIBLE else View.GONE
        adapter =
            TripAdapter(
                compact = allTripsScreen,
                onOpen = ::openTrip,
                onFavorite = { model.toggleFavorite(it) },
            )
        binding.tripRecycler.layoutManager =
            if (allTripsScreen) LinearLayoutManager(this)
            else GridLayoutManager(this, if (resources.configuration.screenWidthDp >= 360) 2 else 1)
        binding.tripRecycler.adapter = adapter
        binding.userIconTop.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
        binding.btnCreateTrip.setOnClickListener {
            startActivity(Intent(this, AddTripActivity::class.java))
        }
        binding.btnAll.setOnClickListener {
            tab = "all"
            render()
        }
        binding.btnUpcoming.setOnClickListener {
            tab = "upcoming"
            render()
        }
        binding.btnPast.setOnClickListener {
            tab = "past"
            render()
        }
        binding.searchIcon.setOnClickListener { search() }
        binding.searchPlaces.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                search()
                true
            } else false
        }
        model.trips.observe(this) { state ->
            when (state) {
                UiState.Loading -> showState(loading = true)
                UiState.Error ->
                    showState(message = getString(R.string.load_error), retry = { model.load() })
                is UiState.Content -> {
                    trips = state.data
                    render()
                }
            }
        }
        observeAction(model)
        val profile = ViewModelProvider(this)[ProfileViewModel::class.java]
        profile.profile.observe(this) { state ->
            if (state is UiState.Content) {
                if (!allTripsScreen)
                    binding.greetingText.text =
                        state.data.firstName.takeIf(String::isNotBlank)?.let {
                            getString(R.string.hello_name, it)
                        } ?: getString(R.string.welcome_back)
                com.example.triptogether.drive.DriveImages.load(
                    binding.userIconTop,
                    state.data.imageUrl,
                    R.drawable.ic_ui_user,
                    circle = true,
                )
            }
        }
        profile.load()
    }

    override fun onResume() {
        super.onResume()
        if (::model.isInitialized) model.load()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("tab", tab)
        super.onSaveInstanceState(outState)
    }

    private fun render() {
        listOf(
                binding.btnAll to "all",
                binding.btnUpcoming to "upcoming",
                binding.btnPast to "past",
            )
            .forEach { (view, name) -> view.isSelected = tab == name }
        val shown = TripFilters.filter(trips, status = tab)
        binding.tripCountLabel.text = getString(R.string.trip_count, shown.size)
        adapter.submitList(shown)
        showState(message = if (shown.isEmpty()) getString(R.string.no_trips_yet) else null)
    }

    private fun openTrip(trip: Trip) {
        startActivity(Intent(this, TripSummaryActivity::class.java).putExtra("trip", trip))
    }

    private fun search() {
        startActivity(
            Intent(this, SearchResultsActivity::class.java)
                .putExtra("query", binding.searchPlaces.text.toString())
        )
    }
}
