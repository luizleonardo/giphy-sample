package com.example.giphysample.ui.main.trending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.giphysample.R
import com.example.giphysample.ui.ViewData
import com.example.giphysample.ui.main.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_trending.*
import kotlinx.android.synthetic.main.fragment_trending.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class TrendingFragment : BaseFragment() {

    companion object {
        private const val COLUMNS = 2

        @JvmStatic
        fun newInstance(): TrendingFragment = TrendingFragment()
    }

    private val trendingViewModel: TrendingViewModel by viewModel()

    private val trendingAdapter = TrendingAdapter()

    override fun layoutResource(): Int = R.layout.fragment_trending

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        with(trendingViewModel) {
            viewLifecycleOwner.lifecycle.addObserver(this)
            observeTrendingGifList(this)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trendingViewModel.fetchTrendingGifs(null, null)
    }

    override fun setupView(view: View) {
        super.setupView(view)
        view.fragment_trending_recycler_view.apply {
            layoutManager = GridLayoutManager(view.context, COLUMNS)
            adapter = trendingAdapter
        }
    }

    private fun observeTrendingGifList(trendingViewModel: TrendingViewModel) {
        trendingViewModel.liveDataTrendingGifList.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    ViewData.Status.LOADING -> {
                        fragment_trending_progress_bar.show()
                        Toast.makeText(context, "LOADING LIST", Toast.LENGTH_SHORT).show()
                    }
                    ViewData.Status.COMPLETE -> {
                        fragment_trending_progress_bar.hide()
                        trendingAdapter.submitList(it.data)
                    }
                    ViewData.Status.ERROR -> {
                        fragment_trending_progress_bar.hide()
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}