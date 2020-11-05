package com.example.giphysample.di

import com.example.giphysample.BuildConfig
import com.example.giphysample.BuildConfig.API_KEY
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit.SECONDS

private const val API_KEY_PARAMETER = "api_key"

fun createNetworkClient(baseUrl: String) = retrofitClient(baseUrl, httpClient())

class BasicAuthInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl =
            request.url.newBuilder().addQueryParameter(API_KEY_PARAMETER, API_KEY).build()
        return chain.proceed(request.newBuilder().url(newUrl).build())
    }

}

private fun httpClient(): OkHttpClient {
    val clientBuilder = OkHttpClient.Builder()
    if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).let {
            it.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(it)
        }
    }
    clientBuilder.also {
        it.addInterceptor(BasicAuthInterceptor())
        it.connectTimeout(120, SECONDS)
        it.readTimeout(120, SECONDS)
        it.writeTimeout(120, SECONDS)
    }
    return clientBuilder.build()
}

private fun retrofitClient(baseUrl: String, httpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()