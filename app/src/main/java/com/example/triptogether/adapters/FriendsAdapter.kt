package com.example.triptogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.triptogether.R
import com.example.triptogether.model.Friend

class FriendsAdapter(friends: List<Friend> = emptyList()) :
    ListAdapter<Friend, FriendsAdapter.Holder>(Diff) {
    init {
        submitList(friends.toList())
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.ivProfile)
        val name: TextView = view.findViewById(R.id.tvName)
        val role: TextView = view.findViewById(R.id.tvRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val friend = getItem(position)
        holder.name.text = friend.name
        holder.role.text = friend.role
        com.example.triptogether.drive.DriveImages.load(
            holder.avatar,
            friend.imageUrl,
            R.drawable.ic_ui_user,
            circle = true,
        )
    }

    private object Diff : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend) = oldItem.uid == newItem.uid

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend) = oldItem == newItem
    }
}
