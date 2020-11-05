package com.example.giphysample.data.repository

import com.example.giphysample.data.api.RemoteGiphyApi
import com.example.giphysample.data.entities.GiphyTrendingHolder
import io.reactivex.Observable

class GiphyRepository(private val apiService: RemoteGiphyApi) {

    fun fetchTrendingList(limit: Int?, offset: Int?): Observable<GiphyTrendingHolder> =
        apiService.getTrendingGifs(limit, offset)

    fun fetchSearch(limit: Int?, offset: Int?, query: String?): Observable<GiphyTrendingHolder> =
        apiService.searchGifsByKeyWordAsync(limit, offset, query)
}