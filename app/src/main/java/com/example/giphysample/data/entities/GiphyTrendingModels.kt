package com.example.giphysample.data.entities

import com.google.gson.annotations.SerializedName

data class GiphyTrendingHolder(val data: List<GiphyTrendingItem>)

data class GiphyTrendingItem(
    var id: String? = null,
    var url: String? = null,
    val images: GiphyItemImages
)

data class GiphyItemImages(
    @SerializedName("fixed_height_still") val stillImage: ImageMediumStill,
    @SerializedName("fixed_height") val gif: ImageMediumGif
)

data class ImageMediumStill(val url: String)

data class ImageMediumGif(val url: String)
