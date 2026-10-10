package com.example.triptogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.triptogether.R
import com.example.triptogether.model.Trip

/** Presentation only: navigation and writes are handled by the owning screen. */
class TripAdapter(
    trips: List<Trip> = emptyList(),
    private val compact: Boolean = false,
    private val interactive: Boolean = true,
    private val onOpen: (Trip) -> Unit = {},
    private val onFavorite: (Trip) -> Unit = {},
) : ListAdapter<Trip, TripAdapter.Holder>(Diff) {
    init {
        submitList(trips.toList())
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivTripImage)
        val name: TextView = view.findViewById(R.id.tvTripName)
        val dates: TextView = view.findViewById(R.id.tvTripDates)
        val country: TextView = view.findViewById(R.id.tvCountryName)
        val favorite: ImageView = view.findViewById(R.id.ivFavorite)
        val unread: View = view.findViewById(R.id.unreadDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(
            LayoutInflater.from(parent.context)
                .inflate(
                    if (compact) R.layout.item_favorite_trip else R.layout.item_trip,
                    parent,
                    false,
                )
        )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val trip = getItem(position)
        holder.name.text = trip.name
        holder.country.text = trip.country
        holder.dates.text =
            holder.itemView.context.getString(R.string.trip_dates, trip.startDate, trip.endDate)
        if (compact) {
            val start = com.example.triptogether.utilities.TripFilters.date(trip.startDate)
            val end = com.example.triptogether.utilities.TripFilters.date(trip.endDate)
            if (
                start != null && end != null && start.month == end.month && start.year == end.year
            ) {
                val month =
                    end.format(
                        java.time.format.DateTimeFormatter.ofPattern(
                            "MMM uuuu",
                            java.util.Locale.getDefault(),
                        )
                    )
                holder.dates.text = "${start.dayOfMonth}–${end.dayOfMonth} $month"
            }
        }
        com.example.triptogether.drive.DriveImages.load(
            holder.image,
            trip.imageUrl,
            R.drawable.default_trip_image,
            circle = false,
        )
        holder.unread.visibility = if (trip.hasUnreadMessages) View.VISIBLE else View.GONE
        holder.favorite.isEnabled = interactive
        holder.favorite.setImageResource(
            if (trip.isFavorite) R.drawable.ic_ui_heart_filled else R.drawable.ic_ui_heart
        )
        holder.favorite.setColorFilter(
            ContextCompat.getColor(
                holder.itemView.context,
                if (compact) R.color.on_brand else R.color.ink,
            )
        )
        holder.favorite.contentDescription =
            holder.itemView.context.getString(
                if (trip.isFavorite) R.string.remove_favorite else R.string.add_favorite
            )
        holder.itemView.setOnClickListener { if (interactive) onOpen(trip) }
        holder.favorite.setOnClickListener { if (interactive) onFavorite(trip) }
    }

    private object Diff : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip) = oldItem == newItem
    }
}
