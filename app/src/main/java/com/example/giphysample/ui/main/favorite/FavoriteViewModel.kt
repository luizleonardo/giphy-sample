package com.example.giphysample.ui.main.favorite

import androidx.lifecycle.LifecycleObserver
import com.example.giphysample.data.entities.GiphyImageItem
import com.example.giphysample.data.repository.RoomRepository
import com.example.giphysample.ui.ViewData
import com.example.giphysample.ui.main.LiveEvent
import com.example.giphysample.ui.main.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class FavoriteViewModel(private val roomRepository: RoomRepository) : BaseViewModel(),
    LifecycleObserver {

    val liveDataAddFavorite = LiveEvent<ViewData<String>>()

    val liveDataFavoritesList = LiveEvent<ViewData<List<GiphyImageItem>>>()

    fun addToFavorites(data: GiphyImageItem) {
        compositeDisposable.add(
            roomRepository.addToFavorite(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    liveDataAddFavorite.value =
                        ViewData(ViewData.Status.LOADING, "Adding to favorite...")
                }
                .subscribe(
                    {
                        liveDataAddFavorite.value =
                            ViewData(status = ViewData.Status.SUCCESS, data = "Added to favorite")
                    }, {
                        liveDataAddFavorite.value = ViewData(ViewData.Status.ERROR, error = it)
                    }
                )
        )
    }

    fun removeFromFavorites(data: GiphyImageItem) {
        compositeDisposable.add(
            roomRepository.removeFromFavorite(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    liveDataAddFavorite.value =
                        ViewData(ViewData.Status.LOADING, "Removing from favorite...")
                }
                .subscribe(
                    {
                        liveDataAddFavorite.value = ViewData(
                            status = ViewData.Status.SUCCESS,
                            data = "Removed from favorite"
                        )
                    }, {
                        liveDataAddFavorite.value = ViewData(ViewData.Status.ERROR, error = it)
                    }
                )
        )
    }

    fun getFavorites() {
        compositeDisposable.add(
            roomRepository.getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    liveDataFavoritesList.value =
                        ViewData(ViewData.Status.LOADING)
                }
                .subscribeWith(object : DisposableObserver<List<GiphyImageItem>>() {
                    override fun onError(error: Throwable) {
                        liveDataFavoritesList.value = ViewData(ViewData.Status.ERROR, error = error)
                    }

                    override fun onComplete() {
                        liveDataFavoritesList.value =
                            ViewData(
                                status = ViewData.Status.COMPLETE,
                                data = liveDataFavoritesList.value?.data
                            )
                    }

                    override fun onNext(data: List<GiphyImageItem>) {
                        liveDataFavoritesList.value =
                            ViewData(status = ViewData.Status.SUCCESS, data = data)
                    }

                })
        )
    }
}