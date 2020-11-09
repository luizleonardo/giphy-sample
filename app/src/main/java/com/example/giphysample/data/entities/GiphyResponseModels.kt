package com.example.giphysample.data.entities

import com.google.gson.annotations.SerializedName

data class GiphyResponseHolder(val data: List<GiphyImageItem>)

data class GiphyImageItem(
    var id: String? = null,
    var url: String? = null,
    @SerializedName("images")
    val imageUrl: GiphyItemImageUrl
)

data class GiphyItemImageUrl(
    @SerializedName("fixed_height_still") val stillImage: ImageMediumStill,
    @SerializedName("fixed_height") val gif: ImageMediumGif
)

data class ImageMediumStill(val url: String)

data class ImageMediumGif(val url: String)
