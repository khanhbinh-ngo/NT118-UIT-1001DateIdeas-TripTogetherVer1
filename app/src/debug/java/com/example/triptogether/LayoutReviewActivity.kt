package com.example.triptogether

import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.triptogether.adapters.TripAdapter
import com.example.triptogether.model.Trip
import com.example.triptogether.ui.PlannerActivity

/** Debug-only host: real layouts with local fixtures, never writes to Firebase. */
class LayoutReviewActivity : PlannerActivity() {
    override fun attachBaseContext(base: android.content.Context) {
        val config = android.content.res.Configuration(base.resources.configuration)
        if (
            base
                .getSharedPreferences("appearance", MODE_PRIVATE)
                .getBoolean("reviewLargeText", false)
        )
            config.fontScale = 1.3f
        super.attachBaseContext(base.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent.getBooleanExtra("lightTheme", false)) {
            getSharedPreferences("appearance", MODE_PRIVATE)
                .edit()
                .putInt("nightMode", androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)
                .commit()
        }
        super.onCreate(savedInstanceState)
        setContentView(intent.getIntExtra("layout", R.layout.activity_main))
        findViewById<RecyclerView>(R.id.tripRecycler)?.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter =
                TripAdapter(
                    listOf(
                        Trip(
                            id = "preview-dalat",
                            name = "Weekend in Đà Lạt",
                            country = "Vietnam",
                            startDate = "12/10/2026",
                            endDate = "15/10/2026",
                        ),
                        Trip(
                            id = "preview-kyoto",
                            name = "Discover Kyoto",
                            country = "Japan",
                            startDate = "21/11/2026",
                            endDate = "26/11/2026",
                        ),
                    ),
                    interactive = false,
                )
        }
        findViewById<RecyclerView>(R.id.recyclerFavorites)?.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter =
                TripAdapter(
                    listOf(
                        Trip(
                            id = "favorite-preview",
                            name = "A journey with friends",
                            country = "Vietnam",
                            startDate = "15/10/2026",
                            endDate = "18/10/2026",
                        )
                    ),
                    compact = true,
                    interactive = false,
                )
        }
        findViewById<android.widget.LinearLayout>(R.id.notificationContent)
            ?.addView(
                TextView(this).apply {
                    text = getString(R.string.unread_trip, "Weekend in Đà Lạt")
                    setTextAppearance(R.style.TextAppearance_Trip_Body)
                    setTextColor(
                        androidx.core.content.ContextCompat.getColor(context, R.color.on_brand)
                    )
                    setBackgroundColor(
                        androidx.core.content.ContextCompat.getColor(context, R.color.brand)
                    )
                    setPadding(24, 24, 24, 24)
                }
            )
        findViewById<TextView>(R.id.tripCountLabel)?.text = getString(R.string.trip_count, 2)
    }
}
