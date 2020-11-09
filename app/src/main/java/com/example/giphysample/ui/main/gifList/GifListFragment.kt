package com.example.giphysample.ui.main.gifList

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.example.giphysample.R
import com.example.giphysample.data.entities.GiphyImageItem
import com.example.giphysample.ext.gone
import com.example.giphysample.ext.startShowAnimation
import com.example.giphysample.ext.visible
import com.example.giphysample.ui.ViewData
import com.example.giphysample.ui.main.GifListAdapter
import com.example.giphysample.ui.main.GifListViewHolder
import com.example.giphysample.ui.main.base.BaseFragment
import com.example.giphysample.ui.main.favorite.FavoriteViewModel
import com.example.giphysample.ui.main.gifList.RxSearchObservable.DEBOUNCE
import com.google.android.material.snackbar.Snackbar
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

class GifListFragment : BaseFragment(), GifListViewHolder.FavoriteCallback {

    companion object {
        private const val COLUMNS = 2

        @JvmStatic
        fun newInstance(): GifListFragment = GifListFragment()
    }

    private val gifListViewModel: GifListViewModel by viewModel()
    private val favoriteViewModel: FavoriteViewModel by viewModel()

    private val gifListAdapter = GifListAdapter(this@GifListFragment)
    private val loadingAdapter = LoadingAdapter()
    private val compositeDisposable = CompositeDisposable()
    private var lastSearch: String? = null
    private var appCompatImageViewClose: AppCompatImageView? = null
    private var searchViewEditText: EditText? = null
    private var snackBar: Snackbar? = null
    private var alertDialog: AlertDialog? = null

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

        with(favoriteViewModel) {
            viewLifecycleOwner.lifecycle.addObserver(this)
            observeFavorite(this)
        }

        return view
    }


    private fun observeFavorite(favoriteViewModel: FavoriteViewModel) {
        favoriteViewModel.liveDataAddFavorite.observe(viewLifecycleOwner, {
            when (it?.status) {
                ViewData.Status.LOADING -> {
                    showProgressDialog(it.data ?: "Loading")
                }
                ViewData.Status.SUCCESS -> {
                    dismissProgress()
                    snackBar?.run {
                        if (!isShown) this.show()
                        this.view.findViewById<AppCompatTextView>(R.id.snackbar_text).text = it.data
                    }
                }
                ViewData.Status.ERROR -> {
                    dismissProgress()
                    snackBar?.run {
                        this.view.findViewById<AppCompatTextView>(R.id.snackbar_text).text =
                            it.error?.message
                        if (!isShown) this.show() else this.dismiss()
                    }
                }
            }
        })
    }

    private fun observeSearch(gifListViewModel: GifListViewModel) {
        gifListViewModel.liveDataSearch.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    ViewData.Status.LOADING -> {
                        custom_view_search_view_progress.visible()
                        fragment_gif_list_progress_bar.visible()
                        appCompatImageViewClose?.gone()
                        fragment_gif_list_text_view_label.gone()
                        fragment_gif_list_recycler_view.gone()
                    }
                    ViewData.Status.COMPLETE -> {
                        custom_view_search_view_progress.gone()
                        fragment_gif_list_progress_bar.gone()
                        appCompatImageViewClose?.visible()
                        fragment_gif_list_recycler_view.visible()
                        fragment_gif_list_text_view_label.visible()
                        fragment_gif_list_text_view_label.text =
                            getString(R.string.fragment_gif_list_search_label)
                        fragment_gif_list_recycler_view.startShowAnimation()
                        gifListAdapter.submitList(it.data)
                    }
                    ViewData.Status.ERROR -> {
                        appCompatImageViewClose?.visible()
                        fragment_gif_list_text_view_label.gone()
                        fragment_gif_list_progress_bar.gone()
                        custom_view_search_view_progress.gone()
                        fragment_gif_list_recycler_view.gone()
                    }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        if (!lastSearch.isNullOrEmpty()) {
            gifListViewModel.search(query = lastSearch)
            return
        }
        gifListViewModel.fetchTrendingGifs()
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
            val concatAdapter = ConcatAdapter(
                gifListAdapter,
                loadingAdapter,
            )
            layoutManager = GridLayoutManager(view.context, COLUMNS)
            adapter = concatAdapter
        }
        view.custom_view_search_view.apply {
            maxWidth = Integer.MAX_VALUE
            appCompatImageViewClose =
                this.findViewById(R.id.search_close_btn) as? AppCompatImageView
            searchViewEditText = this.findViewById(R.id.search_src_text) as? EditText
            searchViewEditText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
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
        setupSnackBar(view)
        setupAlertDialog(view)
    }

    private fun setupAlertDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.setView(layoutInflater.inflate(R.layout.custom_dialog, null))
        alertDialog = builder.create()
    }

    private fun setupSnackBar(view: View) {
        snackBar = Snackbar.make(
            view.fragment_gif_list_content_holder,
            "",
            Snackbar.LENGTH_SHORT
        )
    }

    private fun observeTrendingGifList(gifListViewModel: GifListViewModel) {
        gifListViewModel.liveDataImageGifs.observe(
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
                        fragment_gif_list_text_view_label.text =
                            getString(R.string.fragment_gif_list_trending_label)
                        gifListAdapter.submitList(it.data)
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

    override fun onFavoriteAdd(data: GiphyImageItem) {
        favoriteViewModel.addToFavorites(data)
    }

    override fun onFavoriteRemove(data: GiphyImageItem) {
        favoriteViewModel.removeFromFavorites(data)
    }

    private fun showProgressDialog(message: String) {
        view?.context?.let {
            alertDialog?.show()
            alertDialog?.findViewById<AppCompatTextView>(R.id.text_progress_bar)?.text = message
        }
    }

    private fun dismissProgress() {
        view?.context?.let {
            alertDialog?.dismiss()
        }
    }
}