package com.example.giphysample.ui.main.trending

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.example.giphysample.R
import com.example.giphysample.data.entities.GiphyTrendingItem
import com.facebook.shimmer.ShimmerFrameLayout

class GifListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val gifImage: AppCompatImageView = itemView.findViewById(R.id.list_item_gif_image)
    private val shimmerFrameLayout: ShimmerFrameLayout =
        itemView.findViewById(R.id.list_item_gif_shimmer_frame_layout)

    fun bind(data: GiphyTrendingItem) {
        shimmerFrameLayout.showShimmer(true)
        Glide.with(gifImage.context)
            .asGif()
            .placeholder(ColorDrawable(Color.DKGRAY))
            .error(R.drawable.vector_broken_image)
            .fallback(R.drawable.vector_broken_image)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .load(data.images.gif.url)
            .listener(
                GiphyImageRequestListener(
                    itemView.context.resources.getInteger(android.R.integer.config_shortAnimTime),
                    object : GiphyImageRequestListener.Callback {
                        override fun onFailure() {
                            shimmerFrameLayout.hideShimmer()
                        }

                        override fun onSuccess() {
                            shimmerFrameLayout.hideShimmer()
                        }
                    })
            )
            .into(gifImage)
    }

    class GiphyImageRequestListener(
        private val animationDuration: Int,
        private val callback: Callback? = null
    ) :
        RequestListener<GifDrawable> {

        override fun onLoadFailed(
            e: GlideException?,
            model: Any,
            target: com.bumptech.glide.request.target.Target<GifDrawable>,
            isFirstResource: Boolean
        ): Boolean {
            callback?.onFailure()
            return false
        }

        override fun onResourceReady(
            resource: GifDrawable,
            model: Any?,
            target: com.bumptech.glide.request.target.Target<GifDrawable>,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            target.onResourceReady(
                resource,
                DrawableCrossFadeTransition(animationDuration, isFirstResource)
            )
            callback?.onSuccess()
            return true
        }

        interface Callback {
            fun onFailure()
            fun onSuccess()
        }

    }

}