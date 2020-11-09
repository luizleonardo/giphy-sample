package com.example.giphysample.data.repository

import com.example.giphysample.data.dao.GiphyImageDao
import com.example.giphysample.data.entities.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class RoomRepository(private val dao: GiphyImageDao) {

    fun addToFavorite(data: GiphyImageItem): Observable<Unit> = Observable.fromCallable {
        dao.add(transformImageItemToImageData(data))
    }.subscribeOn(Schedulers.io())

    fun removeFromFavorite(data: GiphyImageItem): Observable<Unit> = Observable.fromCallable {
        dao.delete(transformImageItemToImageData(data))
    }.subscribeOn(Schedulers.io())

    fun getFavorites(): Observable<List<GiphyImageItem>> = dao.findAll()
        .toObservable()
        .map { return@map transformImageDataToImageItem(it) }
        .subscribeOn(Schedulers.io())

    fun updateGiphyItem(data: List<GiphyImageItem>): Observable<List<GiphyImageItem>> =
        Observable.create {
            dao.findAll()
                .map { giphyEntity ->
                    data.forEach { giphyImageItem ->
                        giphyEntity.map {giphyImageData ->
                            if (giphyImageData.id == giphyImageItem.id)
                                giphyImageItem.isFavorite = true
                        }
                    }
                    it.onNext(data ?: emptyList())
                    it.onComplete()
                }
                .subscribe()
        }

    private fun transformImageItemToImageData(imageItem: GiphyImageItem): GiphyImageData =
        GiphyImageData(
            id = imageItem.id ?: "",
            url = imageItem.url,
            image = GiphyItemImages(
                ImageMediumStillData(imageItem.imageUrl.stillImage.url),
                ImageMediumGifData(imageItem.imageUrl.gif.url)
            )
        )

    private fun transformImageDataToImageItem(imageDataList: List<GiphyImageData>?) =
        imageDataList?.map {
            GiphyImageItem(
                id = it.id,
                url = it.url,
                imageUrl = GiphyItemImageUrl(
                    ImageMediumStill(it.image?.stillImage?.url ?: ""),
                    ImageMediumGif(it.image?.gif?.url ?: "")
                ),
                isFavorite = true
            )
        } ?: ArrayList()

}