package com.example.giphysample.di

import com.example.giphysample.data.api.RemoteGiphyApi
import com.example.giphysample.data.repository.GiphyRepository
import com.example.giphysample.ui.main.trending.TrendingViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private const val BASE_URL = "https://api.giphy.com/v1/gifs/"
private const val RETROFIT_INSTANCE = "Retrofit"

val repositoryModules = module {
    single { GiphyRepository(get()) }
}

val netWorkModules = module {
    single(named(RETROFIT_INSTANCE)) { createNetworkClient(BASE_URL) }
    single { (get(named(RETROFIT_INSTANCE)) as Retrofit).create(RemoteGiphyApi::class.java) }
}

val viewModels = module {
    viewModel {
        TrendingViewModel(get())
    }
}