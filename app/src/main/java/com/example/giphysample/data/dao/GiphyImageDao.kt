package com.example.giphysample.data.dao

import androidx.room.*
import com.example.giphysample.data.entities.GiphyImageData
import io.reactivex.Flowable

@Dao
interface GiphyImageDao {

    @Query("SELECT * FROM Favorites")
    fun findAll(): Flowable<List<GiphyImageData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(giphyImageItem: GiphyImageData)

    @Delete
    fun delete(giphyImageItem: GiphyImageData)
}