package com.example.giphysample.ui.main.favorite

import androidx.lifecycle.LifecycleObserver
import com.example.giphysample.data.entities.GiphyImageItem
import com.example.giphysample.data.repository.RoomRepository
import com.example.giphysample.ui.MutableSingleLiveData
import com.example.giphysample.ui.ViewData
import com.example.giphysample.ui.main.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class FavoriteViewModel(private val roomRepository: RoomRepository) : BaseViewModel(),
    LifecycleObserver {

    val liveDataAddFavorite: MutableSingleLiveData<ViewData<String>> =
        MutableSingleLiveData()

    val liveDataFavoritesList: MutableSingleLiveData<ViewData<List<GiphyImageItem>>> =
        MutableSingleLiveData()

    fun addToFavorites(data: GiphyImageItem) {
        compositeDisposable.add(
            roomRepository.addToFavorite(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    liveDataAddFavorite.value =
                        ViewData(ViewData.Status.LOADING, "Adding to favorite...")
                }
                .subscribeWith(object : DisposableObserver<Unit>() {
                    override fun onError(error: Throwable) {
                        liveDataAddFavorite.value = ViewData(ViewData.Status.ERROR, error = error)
                    }

                    override fun onComplete() {
                        liveDataAddFavorite.value =
                            ViewData(
                                status = ViewData.Status.COMPLETE,
                                data = liveDataAddFavorite.value?.data
                            )
                    }

                    override fun onNext(t: Unit) {
                        liveDataAddFavorite.value =
                            ViewData(status = ViewData.Status.SUCCESS, data = "Added to favorite")
                    }

                })
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
                .subscribeWith(object : DisposableObserver<Unit>() {
                    override fun onError(error: Throwable) {
                        liveDataAddFavorite.value = ViewData(ViewData.Status.ERROR, error = error)
                    }

                    override fun onComplete() {
                        liveDataAddFavorite.value =
                            ViewData(
                                status = ViewData.Status.COMPLETE,
                                data = liveDataAddFavorite.value?.data
                            )
                    }

                    override fun onNext(t: Unit) {
                        liveDataAddFavorite.value =
                            ViewData(
                                status = ViewData.Status.SUCCESS,
                                data = "Removed from favorite"
                            )
                    }

                })
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