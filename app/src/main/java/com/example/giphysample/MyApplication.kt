package com.example.giphysample

import android.app.Application
import com.example.giphysample.di.databaseModule
import com.example.giphysample.di.netWorkModules
import com.example.giphysample.di.repositoryModules
import com.example.giphysample.di.viewModels
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        loadKoin()
    }

    private fun loadKoin() {
        startKoin {
            androidContext(this@MyApplication)
            modules(
                listOf(
                    databaseModule,
                    netWorkModules,
                    repositoryModules,
                    viewModels
                )
            )
        }
    }
}