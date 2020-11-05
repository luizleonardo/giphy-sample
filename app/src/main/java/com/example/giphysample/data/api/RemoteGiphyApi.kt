package com.example.giphysample.data.api

import com.example.giphysample.data.entities.GiphyTrendingHolder
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteGiphyApi {

    @GET("trending")
    fun getTrendingGifs(
        @Query("limit") limit: Int? = 25,
        @Query("offset") offset: Int? = 0,
    ): Observable<GiphyTrendingHolder>

    @GET("search")
    fun searchGifsByQuery(
        @Query("limit") limit: Int? = 25,
        @Query("offset") offset: Int? = 0,
        @Query("q") query: String?
    ): Observable<GiphyTrendingHolder>

}