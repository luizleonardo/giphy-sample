package com.example.giphysample.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Favorites")
data class GiphyImageData(
    @PrimaryKey
    val id: String = "",
    @ColumnInfo(name = "url")
    val url: String? = null,
    @Embedded
    val image: GiphyItemImages? = null,
)

data class GiphyItemImages(
    @Embedded
    val stillImage: ImageMediumStillData,
    @Embedded
    val gif: ImageMediumGifData
)

data class ImageMediumStillData(
    @ColumnInfo(name = "still_data_url")
    val url: String
)

data class ImageMediumGifData(
    @ColumnInfo(name = "gif_data_url")
    val url: String
)