package com.example.giphysample.data.repository

import com.example.giphysample.data.api.RemoteGiphyApi
import com.example.giphysample.data.entities.GiphyTrendingHolder
import io.reactivex.Observable

class GiphyRepository(private val apiService: RemoteGiphyApi) {

    fun fetchTrendingGifs(limit: Int?, offset: Int?): Observable<GiphyTrendingHolder> =
        apiService.getTrendingGifs(limit, offset)

    fun search(limit: Int?, offset: Int?, query: String?): Observable<GiphyTrendingHolder> =
        apiService.searchGifsByQuery(limit, offset, query)
}