package com.example.giphysample.di

import android.app.Application
import androidx.room.Room
import com.example.giphysample.data.api.RemoteGiphyApi
import com.example.giphysample.data.dao.GiphyDatabase
import com.example.giphysample.data.dao.GiphyImageDao
import com.example.giphysample.data.repository.GiphyRepository
import com.example.giphysample.data.repository.RoomRepository
import com.example.giphysample.ui.main.favorite.FavoriteViewModel
import com.example.giphysample.ui.main.trending.GifListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private const val BASE_URL = "https://api.giphy.com/v1/gifs/"
private const val RETROFIT_INSTANCE = "Retrofit"
private const val DATA_BASE_NAME = "favorites"

val repositoryModules = module {
    single { GiphyRepository(get()) }
    single { RoomRepository(get()) }
}

val databaseModule = module {
    fun provideDatabase(application: Application): GiphyDatabase {
        return Room.databaseBuilder(application, GiphyDatabase::class.java, DATA_BASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideFavoritesGifsDao(database: GiphyDatabase): GiphyImageDao {
        return database.giphyImageDao
    }

    single { provideDatabase(androidApplication()) }
    single { provideFavoritesGifsDao(get()) }
}

val netWorkModules = module {
    single(named(RETROFIT_INSTANCE)) { createNetworkClient(BASE_URL) }
    single { (get(named(RETROFIT_INSTANCE)) as Retrofit).create(RemoteGiphyApi::class.java) }
}

val viewModels = module {
    viewModel {
        GifListViewModel(get())
    }
    viewModel {
        FavoriteViewModel(get())
    }
}