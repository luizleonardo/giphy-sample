package com.example.giphysample.ui.main.trending

import androidx.lifecycle.LifecycleObserver
import com.example.giphysample.data.entities.GiphyTrendingHolder
import com.example.giphysample.data.entities.GiphyTrendingItem
import com.example.giphysample.data.repository.GiphyRepository
import com.example.giphysample.ui.MutableSingleLiveData
import com.example.giphysample.ui.ViewData
import com.example.giphysample.ui.ViewData.Status.*
import com.example.giphysample.ui.main.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class TrendingViewModel(
    private val giphyRepository: GiphyRepository
) : BaseViewModel(), LifecycleObserver {

    val liveDataTrendingGifList: MutableSingleLiveData<ViewData<List<GiphyTrendingItem>>> =
        MutableSingleLiveData()

    fun fetchTrendingGifs(limit: Int?, offset: Int?) {
        compositeDisposable.add(
            giphyRepository.fetchTrendingList(limit, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { liveDataTrendingGifList.postValue(ViewData(LOADING)) }
                .subscribeWith(object : DisposableObserver<GiphyTrendingHolder>() {
                    override fun onNext(response: GiphyTrendingHolder) {
                        liveDataTrendingGifList.value =
                            ViewData(status = SUCCESS, data = response.data)
                    }

                    override fun onError(error: Throwable) {
                        liveDataTrendingGifList.value = ViewData(ERROR, error = error)
                    }

                    override fun onComplete() {
                        liveDataTrendingGifList.value =
                            ViewData(status = COMPLETE, data = liveDataTrendingGifList.value?.data)
                    }

                })
        )
    }

}