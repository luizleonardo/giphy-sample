package com.example.giphysample.ui.main.gifList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.giphysample.R

class LoadingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isPaging: Boolean = false
        set(value) {
            if (field != value) {
                if (field && !value) {
                    notifyItemRemoved(0)
                } else if (value && !field) {
                    notifyItemInserted(0)
                } else if (field && value) {
                    notifyItemChanged(0)
                }
                field = value
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        LoadingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_holder_loading,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LoadingViewHolder).bind(isPaging)
    }

    override fun getItemCount(): Int = if (isPaging) 1 else 0
}