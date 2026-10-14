package com.example.triptogether

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.NotificationsViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe

class NotificationsActivity : PlannerActivity() {
    private lateinit var model: NotificationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        model = ViewModelProvider(this)[NotificationsViewModel::class.java]
        val content = findViewById<LinearLayout>(R.id.notificationContent)
        findViewById<android.view.View>(R.id.btnMarkAllRead).setOnClickListener {
            model.markAllRead()
        }
        model.items.observe(this) { state ->
            when (state) {
                UiState.Loading -> showState(loading = true)
                UiState.Error ->
                    showState(message = getString(R.string.load_error), retry = { model.load() })
                is UiState.Content -> {
                    content.removeAllViews()
                    showState(
                        message =
                            if (state.data.isEmpty()) getString(R.string.no_unread_chats) else null
                    )
                    state.data.forEach { trip ->
                        val row =
                            TextView(this).apply {
                                setTextAppearance(R.style.TextAppearance_Trip_Body)
                                setTextColor(
                                    androidx.core.content.ContextCompat.getColor(
                                        context,
                                        R.color.on_brand,
                                    )
                                )
                                setBackgroundColor(
                                    androidx.core.content.ContextCompat.getColor(
                                        context,
                                        R.color.brand,
                                    )
                                )
                                val padding = (24 * resources.displayMetrics.density).toInt()
                                setPadding(padding, padding / 2, padding, padding / 2)
                                text = getString(R.string.unread_trip, trip.name)
                                isClickable = true
                                isFocusable = true
                                layoutParams =
                                    LinearLayout.LayoutParams(-1, -2).apply {
                                        bottomMargin =
                                            (5 * resources.displayMetrics.density).toInt()
                                    }
                                setOnClickListener {
                                    startActivity(
                                        Intent(context, GroupChatActivity::class.java)
                                            .putExtra("tripId", trip.id)
                                            .putExtra("tripName", trip.name)
                                    )
                                }
                            }
                        content.addView(row)
                    }
                }
            }
        }
        model.action.observe(this) {
            findViewById<android.view.View>(R.id.btnMarkAllRead).isEnabled =
                it != com.example.triptogether.ui.state.ActionState.Running
        }
        observeAction(model)
    }

    override fun onResume() {
        super.onResume()
        if (::model.isInitialized) model.load()
    }
}
