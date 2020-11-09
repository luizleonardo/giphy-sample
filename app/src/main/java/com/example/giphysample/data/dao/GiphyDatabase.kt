package com.example.giphysample.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.giphysample.data.entities.GiphyImageData

@Database(
    entities = [GiphyImageData::class], version = 1, exportSchema = false
)

abstract class GiphyDatabase : RoomDatabase() {
    abstract val giphyImageDao: GiphyImageDao
}