package com.example.triptogether.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.triptogether.R
import com.example.triptogether.model.ChatMessage
import java.util.Locale

class ChatMessageAdapter(
    messages: List<ChatMessage> = emptyList(),
    private val currentEmail: String? = null,
    private val currentUid: String? = null,
) : ListAdapter<ChatMessage, ChatMessageAdapter.Holder>(Diff) {
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val bubble: View = view.findViewById(R.id.messageBubble)
        val sender: TextView = view.findViewById(R.id.tvSender)
        val text: TextView = view.findViewById(R.id.tvText)
        val time: TextView = view.findViewById(R.id.tvTime)
    }

    init {
        submitList(messages.toList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val message = getItem(position)
        val mine =
            if (message.senderUid.isNotBlank()) message.senderUid == currentUid
            else message.sender == currentEmail
        holder.sender.text = message.sender
        holder.text.text = message.message
        holder.time.text =
            java.time.Instant.ofEpochMilli(message.timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))
        holder.bubble.setBackgroundResource(
            if (mine) R.drawable.message_bubble_sent else R.drawable.message_bubble_bg
        )
        val density = holder.itemView.resources.displayMetrics.density
        holder.bubble.layoutParams =
            (holder.bubble.layoutParams as FrameLayout.LayoutParams).apply {
                gravity = if (mine) Gravity.END else Gravity.START
                width =
                    ((holder.itemView.resources.configuration.screenWidthDp - 80).coerceAtMost(
                            320
                        ) * density)
                        .toInt()
            }
        val color =
            ContextCompat.getColor(
                holder.itemView.context,
                if (mine) R.color.canvas else R.color.ink,
            )
        listOf(holder.sender, holder.text, holder.time).forEach { it.setTextColor(color) }
    }

    private object Diff : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
            oldItem.timestamp == newItem.timestamp &&
                oldItem.sender == newItem.sender &&
                oldItem.message == newItem.message

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
            oldItem == newItem
    }
}
