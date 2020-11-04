package com.example.giphysample.di

import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModules = module {
    single(named("remote")) {}
    single(named("local")) {}
}

val useCaseModules = module {
    factory(named("trendingUseCase")) {

    }
}

/*val netWorkModules = module {
    single(named(RETROFIT_INSTANCE)) { createNetworkClient(BASE_URL) }
    single(named(API)) { (get(named(RETROFIT_INSTANCE)) as Retrofit).create(RemoteNewsApi::class.java) }
}*/

/*val viewModels = module {
    viewModel {
        TrendingViewModel()
    }
}*/

private const val BASE_URL = "api.giphy.com/v1/gifs/"
private const val RETROFIT_INSTANCE = "Retrofit"
private const val API = "Api"
private const val GET_NEWS_USECASE = "getNewsUseCase"
private const val REMOTE = "remote response"
private const val DATABASE = "database"