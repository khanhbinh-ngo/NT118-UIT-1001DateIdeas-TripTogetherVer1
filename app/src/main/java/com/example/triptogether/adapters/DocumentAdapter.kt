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
import com.example.triptogether.model.SharedItem
import java.util.*

class DocumentAdapter(
    documentList: List<SharedItem> = emptyList(),
    private val onOpen: (SharedItem) -> Unit = {},
) : ListAdapter<SharedItem, DocumentAdapter.DocumentViewHolder>(Diff) {

    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileIcon: ImageView = itemView.findViewById(R.id.ivFileIcon)
        val fileName: TextView = itemView.findViewById(R.id.tvFileName)
        val fileInfo: TextView = itemView.findViewById(R.id.tvFileInfo)
    }

    init {
        submitList(documentList.toList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = getItem(position)

        holder.fileName.text = document.name

        val dateStr =
            java.time.Instant.ofEpochMilli(document.timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .format(
                    java.time.format.DateTimeFormatter.ofPattern(
                        "dd MMM yyyy, HH:mm",
                        Locale.getDefault(),
                    )
                )
        holder.fileInfo.text =
            holder.itemView.context.getString(
                R.string.file_uploaded_by,
                document.uploadedBy,
                dateStr,
            )

        val extension = document.name.substringAfterLast('.', "").lowercase(Locale.ROOT)

        val iconRes =
            when (extension) {
                "pdf" -> R.drawable.ic_pdf
                "jpg",
                "jpeg",
                "png" -> R.drawable.ic_image
                else -> R.drawable.ic_file
            }
        holder.fileIcon.setImageResource(iconRes)

        holder.itemView.setOnClickListener { onOpen(document) }
    }

    private object Diff : DiffUtil.ItemCallback<SharedItem>() {
        override fun areItemsTheSame(oldItem: SharedItem, newItem: SharedItem) =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: SharedItem, newItem: SharedItem) =
            oldItem == newItem
    }
}
