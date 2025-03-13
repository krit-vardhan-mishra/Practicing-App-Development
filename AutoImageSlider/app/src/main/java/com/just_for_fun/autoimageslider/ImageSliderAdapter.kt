package com.just_for_fun.autoimageslider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageSliderAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_poster_page, parent, false)
        // Important: We must use match_parent for width to avoid the ViewPager2 exception
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        // Get the actual position in the circular list
        val actualPosition = position % images.size

        // Load image with Glide for better memory management
        Glide.with(holder.itemView)
            .load(images[actualPosition])
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        // Return a very large number to create the infinite effect
        return Int.MAX_VALUE
    }

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}