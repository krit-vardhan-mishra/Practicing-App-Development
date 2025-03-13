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
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val actualPosition = position % images.size

        Glide.with(holder.itemView)
            .load(images[actualPosition])
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}