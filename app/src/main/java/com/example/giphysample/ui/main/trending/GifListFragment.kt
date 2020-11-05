package com.example.giphysample.ui.main.trending

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.giphysample.R
import com.example.giphysample.ext.gone
import com.example.giphysample.ext.startShowAnimation
import com.example.giphysample.ext.visible
import com.example.giphysample.ui.ViewData
import com.example.giphysample.ui.main.base.BaseFragment
import com.example.giphysample.ui.main.trending.RxSearchObservable.DEBOUNCE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_gif_list.*
import kotlinx.android.synthetic.main.fragment_gif_list.view.*
import kotlinx.android.synthetic.main.layout_search_view.*
import kotlinx.android.synthetic.main.layout_search_view.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit.MILLISECONDS

class GifListFragment : BaseFragment() {

    companion object {
        private const val COLUMNS = 2

        @JvmStatic
        fun newInstance(): GifListFragment = GifListFragment()
    }

    private val gifListViewModel: GifListViewModel by viewModel()

    private val trendingAdapter = GifListAdapter()
    private val compositeDisposable = CompositeDisposable()
    private var lastSearch: String? = null
    private var appCompatImageViewClose: AppCompatImageView? = null
    private var searchViewEditText: EditText? = null

    override fun layoutResource(): Int = R.layout.fragment_gif_list

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        with(gifListViewModel) {
            viewLifecycleOwner.lifecycle.addObserver(this)
            observeTrendingGifList(this)
            observeSearch(this)
        }

        return view
    }

    private fun observeSearch(gifListViewModel: GifListViewModel) {
        gifListViewModel.liveDataSearch.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    ViewData.Status.LOADING -> {
                        custom_view_search_view_progress.visible()
                        appCompatImageViewClose?.gone()
                        fragment_gif_list_text_view_label.gone()
                        fragment_gif_list_recycler_view.gone()
                    }
                    ViewData.Status.COMPLETE -> {
                        custom_view_search_view_progress.gone()
                        appCompatImageViewClose?.visible()
                        fragment_gif_list_recycler_view.visible()
                        fragment_gif_list_text_view_label.visible()
                        fragment_gif_list_text_view_label.text = getString(R.string.fragment_gif_list_search_label)
                        fragment_gif_list_recycler_view.startShowAnimation()
                        trendingAdapter.submitList(it.data)
                    }
                    ViewData.Status.ERROR -> {
                        appCompatImageViewClose?.visible()
                        fragment_gif_list_text_view_label.gone()
                        custom_view_search_view_progress.gone()
                        fragment_gif_list_recycler_view.gone()
                    }
                }
            }
        )
    }

    private fun observeSearchView(searchView: SearchView) {
        compositeDisposable.add(RxSearchObservable.fromView(searchView)
            .debounce(DEBOUNCE, MILLISECONDS)
            .filter { query -> query.isNotEmpty() && query != lastSearch }
            .map { query -> query.toLowerCase(Locale.getDefault()).trim() }
            .switchMap { query -> Observable.just(query) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    this.lastSearch = it
                    gifListViewModel.search(query = it)
                },
                {
                    // TODO show error
                })
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gifListViewModel.fetchTrendingGifs()
    }

    override fun setupView(view: View) {
        super.setupView(view)
        view.fragment_gif_list_recycler_view.apply {
            layoutManager = GridLayoutManager(view.context, COLUMNS)
            adapter = trendingAdapter
        }
        view.custom_view_search_view.apply {
            maxWidth = Integer.MAX_VALUE
            appCompatImageViewClose =
                this.findViewById(R.id.search_close_btn) as? AppCompatImageView
            searchViewEditText = this.findViewById(R.id.search_src_text) as? EditText
            searchViewEditText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) gifListViewModel.fetchTrendingGifs()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            if (!lastSearch.isNullOrEmpty()) {
                setQuery(lastSearch, false)
                appCompatImageViewClose?.visible()
            }
            observeSearchView(this)
        }
    }

    private fun observeTrendingGifList(gifListViewModel: GifListViewModel) {
        gifListViewModel.liveDataTrendingGifs.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    ViewData.Status.LOADING -> {
                        fragment_gif_list_progress_bar.visible()
                        fragment_gif_list_text_view_label.gone()
                        fragment_gif_list_recycler_view.gone()
                    }
                    ViewData.Status.COMPLETE -> {
                        fragment_gif_list_progress_bar.gone()
                        fragment_gif_list_recycler_view.visible()
                        fragment_gif_list_text_view_label.visible()
                        fragment_gif_list_recycler_view.startShowAnimation()
                        fragment_gif_list_text_view_label.text = getString(R.string.fragment_gif_list_trending_label)
                        trendingAdapter.submitList(it.data)
                    }
                    ViewData.Status.ERROR -> {
                        fragment_gif_list_text_view_label.gone()
                        fragment_gif_list_progress_bar.gone()
                        fragment_gif_list_recycler_view.gone()
                    }
                }
            }
        )
    }
}