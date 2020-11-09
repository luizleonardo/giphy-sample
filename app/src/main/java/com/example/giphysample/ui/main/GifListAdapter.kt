package com.example.giphysample.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.giphysample.R
import com.example.giphysample.data.entities.GiphyImageItem

class GifListAdapter(
    private val favoriteCallback: GifListViewHolder.FavoriteCallback
) :
    ListAdapter<GiphyImageItem, GifListViewHolder>(
        object :
            DiffUtil.ItemCallback<GiphyImageItem>() {
            override fun areItemsTheSame(oldItem: GiphyImageItem, newItem: GiphyImageItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: GiphyImageItem, newItem: GiphyImageItem) =
                oldItem == newItem
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifListViewHolder {
        return GifListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_gif, parent, false),
            favoriteCallback
        )
    }

    override fun onBindViewHolder(holder: GifListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}