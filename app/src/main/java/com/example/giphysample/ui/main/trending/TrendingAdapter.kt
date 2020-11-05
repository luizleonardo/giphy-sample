package com.example.giphysample.ui.main.trending

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.giphysample.R
import com.example.giphysample.data.entities.GiphyTrendingItem

class TrendingAdapter() : ListAdapter<GiphyTrendingItem, TrendingViewHolder>(
    object :
        DiffUtil.ItemCallback<GiphyTrendingItem>() {
        override fun areItemsTheSame(oldItem: GiphyTrendingItem, newItem: GiphyTrendingItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: GiphyTrendingItem, newItem: GiphyTrendingItem) =
            oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        return TrendingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_gif, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}