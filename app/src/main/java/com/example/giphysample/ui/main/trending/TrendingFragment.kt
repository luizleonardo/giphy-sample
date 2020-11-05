package com.example.giphysample.ui.main.trending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.giphysample.R
import com.example.giphysample.ui.ViewData
import com.example.giphysample.ui.main.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class TrendingFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance(): TrendingFragment = TrendingFragment()
    }

    private val trendingViewModel: TrendingViewModel by viewModel()

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
    }

    private fun observeTrendingGifList(trendingViewModel: TrendingViewModel) {
        trendingViewModel.liveDataTrendingGifList.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    ViewData.Status.LOADING -> {
                        Toast.makeText(context, "LOADING LIST", Toast.LENGTH_SHORT).show()
                    }
                    ViewData.Status.COMPLETE -> {
                        Toast.makeText(context, "COMPLETE LIST: ${it.data}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ViewData.Status.ERROR -> {
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}