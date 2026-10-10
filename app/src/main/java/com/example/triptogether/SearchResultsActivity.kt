package com.example.triptogether

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triptogether.adapters.TripAdapter
import com.example.triptogether.databinding.ActivitySearchResultsBinding
import com.example.triptogether.model.Trip
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.TripsViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe
import com.example.triptogether.utilities.TripFilters
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SearchResultsActivity : PlannerActivity() {
    private lateinit var binding: ActivitySearchResultsBinding
    private var trips = emptyList<Trip>()
    private lateinit var adapter: TripAdapter
    private lateinit var model: TripsViewModel
    private var destination = ""
    private var status = "all"
    private var from: LocalDate? = null
    private var to: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        model = androidx.lifecycle.ViewModelProvider(this)[TripsViewModel::class.java]
        adapter =
            TripAdapter(
                compact = true,
                onOpen = {
                    startActivity(
                        android.content
                            .Intent(this, TripSummaryActivity::class.java)
                            .putExtra("trip", it)
                    )
                },
                onFavorite = { model.toggleFavorite(it) },
            )
        binding.rvResults.adapter = adapter
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
        destination = savedInstanceState?.getString("destination").orEmpty()
        status = savedInstanceState?.getString("status") ?: "all"
        from =
            savedInstanceState?.getString("from")?.let {
                runCatching { LocalDate.parse(it) }.getOrNull()
            }
        to =
            savedInstanceState?.getString("to")?.let {
                runCatching { LocalDate.parse(it) }.getOrNull()
            }
        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.resultQuery.setText(
            savedInstanceState?.getString("query") ?: intent.getStringExtra("query")
        )
        binding.resultQuery.doAfterTextChanged { render() }
        binding.btnFilter.setOnClickListener { showFilters() }
        binding.btnBackSearch.setOnClickListener { finish() }
        model.load()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("query", binding.resultQuery.text.toString())
        outState.putString("destination", destination)
        outState.putString("status", status)
        outState.putString("from", from?.toString())
        outState.putString("to", to?.toString())
        super.onSaveInstanceState(outState)
    }

    private fun render() {
        val shown =
            TripFilters.filter(
                trips,
                binding.resultQuery.text.toString(),
                destination,
                status,
                from,
                to,
            )
        binding.resultsCount.text = getString(R.string.trip_count, shown.size)
        adapter.submitList(shown)
        showState(message = if (shown.isEmpty()) getString(R.string.no_results) else null)
    }

    private fun showFilters() {
        val padding = (24 * resources.displayMetrics.density).toInt()
        val form =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(padding, padding, padding, 0)
            }
        val country =
            EditText(this).apply {
                hint = getString(R.string.destination_filter)
                setText(destination)
                minHeight = padding * 2
            }
        val choices = RadioGroup(this)
        val optionIds = listOf(View.generateViewId(), View.generateViewId(), View.generateViewId())
        listOf(R.string.all_trips, R.string.upcoming, R.string.past).forEachIndexed { index, label
            ->
            choices.addView(
                RadioButton(this).apply {
                    id = optionIds[index]
                    setText(label)
                    minHeight = padding * 2
                }
            )
        }
        choices.check(optionIds[listOf("all", "upcoming", "past").indexOf(status).coerceAtLeast(0)])
        var selectedFrom = from
        var selectedTo = to
        val dateFormat = DateTimeFormatter.ofPattern("dd/MM/uuuu")
        val start =
            Button(this).apply {
                text = selectedFrom?.format(dateFormat) ?: getString(R.string.start_date)
            }
        val end =
            Button(this).apply {
                text = selectedTo?.format(dateFormat) ?: getString(R.string.end_date)
            }
        fun chooseDate(current: LocalDate?, callback: (LocalDate) -> Unit) {
            val date = current ?: LocalDate.now()
            DatePickerDialog(
                    this,
                    { _, y, m, d -> callback(LocalDate.of(y, m + 1, d)) },
                    date.year,
                    date.monthValue - 1,
                    date.dayOfMonth,
                )
                .show()
        }
        start.setOnClickListener {
            chooseDate(selectedFrom) {
                selectedFrom = it
                start.text = it.format(dateFormat)
            }
        }
        end.setOnClickListener {
            chooseDate(selectedTo) {
                selectedTo = it
                end.text = it.format(dateFormat)
            }
        }
        form.addView(country)
        form.addView(choices)
        form.addView(start)
        form.addView(end)
        val dialog =
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.filter)
                .setView(form)
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.reset) { _, _ ->
                    destination = ""
                    status = "all"
                    from = null
                    to = null
                    render()
                }
                .setPositiveButton(R.string.apply, null)
                .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (
                    selectedFrom != null &&
                        selectedTo != null &&
                        selectedTo?.isBefore(selectedFrom) == true
                ) {
                    Toast.makeText(this, R.string.date_order_error, Toast.LENGTH_SHORT).show()
                } else {
                    destination = country.text.toString()
                    status =
                        listOf("all", "upcoming", "past")[
                            optionIds.indexOf(choices.checkedRadioButtonId)]
                    from = selectedFrom
                    to = selectedTo
                    render()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }
}
