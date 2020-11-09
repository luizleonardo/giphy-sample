package com.example.giphysample.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.example.giphysample.R
import com.example.giphysample.data.entities.GiphyImageItem
import com.example.giphysample.ext.gone
import com.example.giphysample.ext.visible
import com.facebook.shimmer.ShimmerFrameLayout

class GifListViewHolder(
    itemView: View,
    private val favoriteCallback: FavoriteCallback?
) :
    RecyclerView.ViewHolder(itemView) {

    private val gifImage: AppCompatImageView = itemView.findViewById(R.id.list_item_gif_image)
    private val favoriteButton: AppCompatToggleButton =
        itemView.findViewById(R.id.list_item_gif_favorite)
    private val shimmerFrameLayout: ShimmerFrameLayout =
        itemView.findViewById(R.id.list_item_gif_shimmer_frame_layout)

    private val scaleAnimation = ScaleAnimation(
        0.7f,
        1.0f,
        0.7f,
        1.0f,
        Animation.RELATIVE_TO_SELF,
        0.7f,
        Animation.RELATIVE_TO_SELF,
        0.7f
    ).also {
        it.duration = 500
        it.interpolator = BounceInterpolator()
    }

    fun bind(data: GiphyImageItem) {
        shimmerFrameLayout.showShimmer(true)
        favoriteButton.setOnCheckedChangeListener(null)
        favoriteButton.isChecked = data.isFavorite
        favoriteButton.setOnCheckedChangeListener { button, checked ->
            button?.startAnimation(scaleAnimation)
            if (checked) {
                favoriteCallback?.onFavoriteAdd(data)
                return@setOnCheckedChangeListener
            }
            favoriteCallback?.onFavoriteRemove(data)
            return@setOnCheckedChangeListener
        }
        Glide.with(gifImage.context)
            .asGif()
            .placeholder(ColorDrawable(Color.DKGRAY))
            .error(R.drawable.vector_broken_image)
            .fallback(R.drawable.vector_broken_image)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .load(data.imageUrl.gif.url)
            .listener(
                GiphyImageRequestListener(
                    itemView.context.resources.getInteger(android.R.integer.config_shortAnimTime),
                    object : GiphyImageRequestListener.Callback {
                        override fun onFailure() {
                            shimmerFrameLayout.hideShimmer()
                            favoriteButton.gone()
                        }

                        override fun onSuccess() {
                            shimmerFrameLayout.hideShimmer()
                            favoriteButton.visible()
                        }
                    })
            )
            .into(gifImage)
    }


    interface FavoriteCallback {
        fun onFavoriteAdd(data: GiphyImageItem)
        fun onFavoriteRemove(data: GiphyImageItem)
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