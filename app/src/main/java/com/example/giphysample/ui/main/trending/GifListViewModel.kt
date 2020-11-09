package com.example.giphysample.ui.main.trending

import androidx.lifecycle.LifecycleObserver
import com.example.giphysample.data.entities.GiphyImageItem
import com.example.giphysample.data.entities.GiphyResponseHolder
import com.example.giphysample.data.repository.GiphyRepository
import com.example.giphysample.ui.MutableSingleLiveData
import com.example.giphysample.ui.ViewData
import com.example.giphysample.ui.ViewData.Status.*
import com.example.giphysample.ui.main.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class GifListViewModel(
    private val giphyRepository: GiphyRepository
) : BaseViewModel(), LifecycleObserver {

    val liveDataImageGifs: MutableSingleLiveData<ViewData<List<GiphyImageItem>>> =
        MutableSingleLiveData()

    val liveDataSearch: MutableSingleLiveData<ViewData<List<GiphyImageItem>>> =
        MutableSingleLiveData()

    fun fetchTrendingGifs(limit: Int? = null, offset: Int? = null) {
        compositeDisposable.add(
            giphyRepository.fetchTrendingGifs(limit, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { liveDataImageGifs.postValue(ViewData(LOADING)) }
                .subscribeWith(object : DisposableObserver<GiphyResponseHolder>() {
                    override fun onNext(response: GiphyResponseHolder) {
                        liveDataImageGifs.value =
                            ViewData(status = SUCCESS, data = response.data)
                    }

                    override fun onError(error: Throwable) {
                        liveDataImageGifs.value = ViewData(ERROR, error = error)
                    }

                    override fun onComplete() {
                        liveDataImageGifs.value =
                            ViewData(status = COMPLETE, data = liveDataImageGifs.value?.data)
                    }

                })
        )
    }

    fun search(limit: Int? = null, offset: Int? = null, query: String?) {
        compositeDisposable.add(
            giphyRepository.search(limit, offset, query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { liveDataSearch.postValue(ViewData(LOADING)) }
                .subscribeWith(object : DisposableObserver<GiphyResponseHolder>() {
                    override fun onNext(response: GiphyResponseHolder) {
                        liveDataSearch.value =
                            ViewData(status = SUCCESS, data = response.data)
                    }

                    override fun onError(error: Throwable) {
                        liveDataSearch.value = ViewData(ERROR, error = error)
                    }

                    override fun onComplete() {
                        liveDataSearch.value =
                            ViewData(status = COMPLETE, data = liveDataSearch.value?.data)
                    }

                })
        )
    }

}