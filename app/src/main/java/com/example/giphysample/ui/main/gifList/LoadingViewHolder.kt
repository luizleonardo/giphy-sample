package com.example.giphysample.ui.main.gifList

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.giphysample.ext.gone
import com.example.giphysample.ext.visible
import kotlinx.android.synthetic.main.view_holder_loading.view.*

class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val progress = itemView.view_holder_loading_progress

    fun bind(data: Boolean) {
        if (data) {
            progress.visible()
        } else {
            progress.gone()
        }
    }
}
