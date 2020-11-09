package com.example.giphysample.ui.main.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.giphysample.R
import com.example.giphysample.ui.ViewData
import com.example.giphysample.ui.main.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class FavoriteFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance(): FavoriteFragment {
            return FavoriteFragment()
        }
    }

    private val favoriteViewModel: FavoriteViewModel by viewModel()

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

    private fun observeFavoritesList(favoriteViewModel: FavoriteViewModel) {
        favoriteViewModel.liveDataFavoritesList.observe(viewLifecycleOwner, {
            when (it?.status) {
                ViewData.Status.LOADING -> {
                }
                ViewData.Status.SUCCESS -> {
                    Toast.makeText(context, "LOADED: " + it.data, Toast.LENGTH_SHORT).show()
                }
                ViewData.Status.ERROR -> {
                }
            }
        })
    }

    override fun layoutResource(): Int = R.layout.fragment_favorite

}