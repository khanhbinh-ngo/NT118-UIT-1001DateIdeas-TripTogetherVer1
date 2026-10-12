package com.example.triptogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.triptogether.R
import com.example.triptogether.model.SharedItem

class ImageAdapter(
    images: List<SharedItem> = emptyList(),
    private val onItemClick: (String) -> Unit,
) : ListAdapter<SharedItem, ImageAdapter.ImageViewHolder>(Diff) {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivImageItem)
    }

    init {
        submitList(images.toList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageItem = getItem(position)

        com.example.triptogether.drive.DriveImages.load(
            holder.imageView,
            imageItem.url,
            R.drawable.ic_image_placeholder,
            circle = false,
        )

        holder.itemView.setOnClickListener { onItemClick(imageItem.url) }
    }

    private object Diff : DiffUtil.ItemCallback<SharedItem>() {
        override fun areItemsTheSame(oldItem: SharedItem, newItem: SharedItem) =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: SharedItem, newItem: SharedItem) =
            oldItem == newItem
    }
}
