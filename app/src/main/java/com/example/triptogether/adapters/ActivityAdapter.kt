package com.example.triptogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.triptogether.R
import com.example.triptogether.model.ActivityItem

class ActivityAdapter(private val onEditOrDelete: (ActivityItem, String) -> Unit) :
    ListAdapter<Pair<ActivityItem, String>, ActivityAdapter.Holder>(Diff) {
    fun updateData(activities: List<ActivityItem>, keys: List<String>) {
        submitList(activities.zip(keys))
    }

    fun getCurrentItems(): List<Pair<ActivityItem, String>> = currentList.toList()

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView = view.findViewById(R.id.tvTime)
        val emoji: TextView = view.findViewById(R.id.tvEmoji)
        val description: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_activity_plan, parent, false)
        )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val (item, key) = getItem(position)
        holder.time.text = item.time
        holder.emoji.text = item.emoji
        holder.description.text = item.description
        holder.itemView.setOnLongClickListener {
            onEditOrDelete(item, key)
            true
        }
    }

    private object Diff : DiffUtil.ItemCallback<Pair<ActivityItem, String>>() {
        override fun areItemsTheSame(
            oldItem: Pair<ActivityItem, String>,
            newItem: Pair<ActivityItem, String>,
        ) = oldItem.second == newItem.second

        override fun areContentsTheSame(
            oldItem: Pair<ActivityItem, String>,
            newItem: Pair<ActivityItem, String>,
        ) = oldItem == newItem
    }
}
