package com.example.giphysample.ui.main.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.giphysample.R
import com.example.giphysample.data.entities.GiphyImageItem
import com.example.giphysample.ext.gone
import com.example.giphysample.ext.visible
import com.example.giphysample.ui.ViewData
import com.example.giphysample.ui.main.base.BaseFragment
import com.example.giphysample.ui.main.trending.GifListAdapter
import com.example.giphysample.ui.main.trending.GifListViewHolder
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_favorite.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class FavoriteFragment : BaseFragment(), GifListViewHolder.FavoriteCallback {

    companion object {
        private const val COLUMNS = 2

        @JvmStatic
        fun newInstance(): FavoriteFragment {
            return FavoriteFragment()
        }
    }

    private val favoriteViewModel: FavoriteViewModel by viewModel()

    private val trendingAdapter = GifListAdapter(this@FavoriteFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        with(favoriteViewModel) {
            viewLifecycleOwner.lifecycle.addObserver(this)
            observeFavoritesList(this)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteViewModel.getFavorites()
    }

    override fun setupView(view: View) {
        super.setupView(view)
        view.fragment_favorite_recycler_view.apply {
            layoutManager = GridLayoutManager(view.context, COLUMNS)
            adapter = trendingAdapter
        }
    }

    private fun observeFavoritesList(favoriteViewModel: FavoriteViewModel) {
        favoriteViewModel.liveDataFavoritesList.observe(viewLifecycleOwner, {
            when (it?.status) {
                ViewData.Status.LOADING -> {
                    fragment_favorite_progress_bar.visible()
                    fragment_favorite_recycler_view.gone()
                }
                ViewData.Status.SUCCESS -> {
                    fragment_favorite_progress_bar.gone()
                    fragment_favorite_recycler_view.visible()
                    trendingAdapter.submitList(it.data)
                }
                ViewData.Status.ERROR -> {
                    fragment_favorite_progress_bar.gone()
                    fragment_favorite_recycler_view.gone()
                }
            }
        })
    }

    override fun layoutResource(): Int = R.layout.fragment_favorite

    override fun onFavoriteAdd(data: GiphyImageItem) {
    }

    override fun onFavoriteRemove(data: GiphyImageItem) {
    }

}