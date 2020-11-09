package com.example.giphysample.data.repository

import com.example.giphysample.data.api.RemoteGiphyApi
import com.example.giphysample.data.entities.GiphyResponseHolder
import io.reactivex.Observable

class GiphyRepository(private val apiService: RemoteGiphyApi) {

    fun fetchTrendingGifs(limit: Int?, offset: Int?): Observable<GiphyResponseHolder> =
        apiService.getTrendingGifs(limit, offset)

    fun search(limit: Int?, offset: Int?, query: String?): Observable<GiphyResponseHolder> =
        apiService.searchGifsByQuery(limit, offset, query)
}