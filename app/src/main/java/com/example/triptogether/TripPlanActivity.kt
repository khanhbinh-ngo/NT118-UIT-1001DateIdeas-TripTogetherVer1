package com.example.triptogether

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.triptogether.adapters.ActivityAdapter
import com.example.triptogether.databinding.ActivityTripPlanBinding
import com.example.triptogether.model.ActivityItem
import com.example.triptogether.model.Trip
import com.example.triptogether.ui.AddActivityDialogFragment
import com.example.triptogether.ui.DeleteActivityDialogFragment
import com.example.triptogether.ui.EditActivityDialogFragment
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.PlanViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe
import com.example.triptogether.utilities.TripFilters
import com.example.triptogether.utilities.tripExtra
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class TripPlanActivity : PlannerActivity() {
    private lateinit var binding: ActivityTripPlanBinding

    private lateinit var trip: Trip
    private lateinit var model: PlanViewModel

    private lateinit var tvTripTitle: TextView
    private lateinit var rvActivities: RecyclerView
    private lateinit var btnAddActivity: Button
    private lateinit var btnChange: Button
    private lateinit var btnBack: ImageView
    private lateinit var layoutDays: LinearLayout
    private lateinit var btnPrevDays: ImageView
    private lateinit var btnNextDays: ImageView

    private lateinit var dayList: List<String>
    private var selectedDayIndex = 0
    private var currentDayWindowStart = 0
    private lateinit var activityAdapter: ActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = androidx.lifecycle.ViewModelProvider(this)[PlanViewModel::class.java]
        trip =
            intent.tripExtra()
                ?: run {
                    finish()
                    return
                }

        tvTripTitle = binding.tvTripTitle
        rvActivities = binding.rvActivities
        btnAddActivity = binding.btnAddActivity
        btnChange = binding.btnChange
        btnBack = binding.btnBack
        layoutDays = binding.dayTabsContainer
        btnPrevDays = binding.btnPrevDays
        btnNextDays = binding.btnNextDays

        tvTripTitle.text = trip.name
        dayList = generateDateRange(trip.startDate, trip.endDate)

        if (dayList.isEmpty()) {
            toast(R.string.invalid_trip_dates)
            finish()
            return
        }

        selectedDayIndex =
            (savedInstanceState?.getInt("selectedDay") ?: 0).coerceIn(dayList.indices)
        currentDayWindowStart = selectedDayIndex / 3 * 3
        setupDayTabs()

        activityAdapter = ActivityAdapter { activity, key -> showEditDeleteOptions(activity, key) }

        rvActivities.layoutManager = LinearLayoutManager(this)
        rvActivities.adapter = activityAdapter

        model.items.observe(this) { state ->
            when (state) {
                UiState.Loading -> showState(loading = true)
                UiState.Error ->
                    showState(
                        message = getString(R.string.load_error),
                        retry = { loadActivitiesForDay(selectedDayIndex) },
                    )
                is UiState.Content -> {
                    activityAdapter.submitList(state.data)
                    showState(
                        message =
                            if (state.data.isEmpty()) getString(R.string.no_activities) else null
                    )
                }
            }
        }
        supportFragmentManager.setFragmentResultListener("planChanged", this) { _, _ ->
            loadActivitiesForDay(selectedDayIndex)
        }
        loadActivitiesForDay(selectedDayIndex)

        btnAddActivity.setOnClickListener {
            AddActivityDialogFragment.newInstance(
                    tripId = trip.id,
                    createdBy = trip.createdBy,
                    dayIndex = selectedDayIndex,
                )
                .show(supportFragmentManager, "AddActivityDialog")
        }

        btnChange.setOnClickListener {
            val options =
                arrayOf(getString(R.string.edit_activity), getString(R.string.delete_activity))
            AlertDialog.Builder(this)
                .setTitle(R.string.choose_action)
                .setItems(options) { _, which ->
                    if (which == 0) promptEditActivity() else promptDeleteActivity()
                }
                .show()
        }

        btnBack.setOnClickListener { finish() }

        btnPrevDays.setOnClickListener {
            if (currentDayWindowStart >= 3) {
                currentDayWindowStart -= 3
                setupDayTabs()
            }
        }

        btnNextDays.setOnClickListener {
            if (currentDayWindowStart + 3 < dayList.size) {
                currentDayWindowStart += 3
                setupDayTabs()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("selectedDay", selectedDayIndex)
        super.onSaveInstanceState(outState)
    }

    private fun setupDayTabs() {
        layoutDays.removeAllViews()

        val inflater = layoutInflater
        val end = minOf(currentDayWindowStart + 3, dayList.size)

        for (i in currentDayWindowStart until end) {
            val view = inflater.inflate(R.layout.item_day, layoutDays, false)
            val tvDayNumber = view.findViewById<TextView>(R.id.tvDayNumber)
            val tvDayDate = view.findViewById<TextView>(R.id.tvDayDate)

            tvDayNumber.text = getString(R.string.day_number, i + 1)
            tvDayDate.text = formatDate(dayList[i])

            view.isSelected = i == selectedDayIndex
            view.setBackgroundResource(R.drawable.bg_tab)

            view.setOnClickListener {
                selectedDayIndex = i
                setupDayTabs()
                loadActivitiesForDay(i)
            }

            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            layoutDays.addView(view, params)
        }

        btnPrevDays.visibility = if (currentDayWindowStart > 0) View.VISIBLE else View.INVISIBLE
        btnNextDays.visibility = if (end < dayList.size) View.VISIBLE else View.INVISIBLE
    }

    private fun loadActivitiesForDay(dayIndex: Int) {
        model.load(trip.createdBy, trip.id, dayIndex)
    }

    private fun promptEditActivity() {
        val items = activityAdapter.getCurrentItems()
        if (items.isEmpty()) {
            toast(R.string.no_activities)
            return
        }
        val options = items.map { "${it.first.time} - ${it.first.description}" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle(R.string.choose_edit_activity)
            .setItems(options) { _, index ->
                showEditActivityDialog(items[index].first, items[index].second)
            }
            .show()
    }

    private fun promptDeleteActivity() {
        val items = activityAdapter.getCurrentItems()
        if (items.isEmpty()) {
            toast(R.string.no_activities)
            return
        }
        val options = items.map { "${it.first.time} - ${it.first.description}" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle(R.string.choose_delete_activity)
            .setItems(options) { _, index -> showDeleteActivityDialog(items[index].second) }
            .show()
    }

    private fun showEditActivityDialog(activity: ActivityItem, key: String) {
        EditActivityDialogFragment.newInstance(
                tripId = trip.id,
                createdBy = trip.createdBy,
                dayIndex = selectedDayIndex,
                activityKey = key,
                currentActivity = activity,
            )
            .show(supportFragmentManager, "EditActivityDialog")
    }

    private fun showDeleteActivityDialog(key: String) {
        DeleteActivityDialogFragment.newInstance(
                tripId = trip.id,
                createdBy = trip.createdBy,
                dayIndex = selectedDayIndex,
                activityKey = key,
            )
            .show(supportFragmentManager, "DeleteActivityDialog")
    }

    private fun generateDateRange(startDate: String, endDate: String): List<String> {
        val start = TripFilters.date(startDate) ?: return emptyList()
        val end = TripFilters.date(endDate) ?: return emptyList()
        if (end.isBefore(start) || ChronoUnit.DAYS.between(start, end) > 3650) return emptyList()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.US)
        return generateSequence(start) { it.plusDays(1) }
            .takeWhile { !it.isAfter(end) }
            .map { it.format(formatter) }
            .toList()
    }

    private fun formatDate(date: String): String =
        TripFilters.date(date)?.format(DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault()))
            ?: date

    private fun showEditDeleteOptions(activity: ActivityItem, key: String) {
        val options =
            arrayOf(getString(R.string.edit_activity), getString(R.string.delete_activity))
        AlertDialog.Builder(this)
            .setTitle(R.string.choose_action)
            .setItems(options) { _, which ->
                if (which == 0) showEditActivityDialog(activity, key)
                else showDeleteActivityDialog(key)
            }
            .show()
    }
}
