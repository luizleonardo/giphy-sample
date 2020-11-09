package com.example.giphysample.ui.main.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.giphysample.R
import com.example.giphysample.data.entities.GiphyImageItem
import com.example.giphysample.ext.gone
import com.example.giphysample.ext.visible
import com.example.giphysample.ui.ViewData
import com.example.giphysample.ui.main.GifListAdapter
import com.example.giphysample.ui.main.GifListViewHolder
import com.example.giphysample.ui.main.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
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

    private val gifListAdapter = GifListAdapter(this@FavoriteFragment)

    private var snackBar: Snackbar? = null
    private var alertDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        with(favoriteViewModel) {
            viewLifecycleOwner.lifecycle.addObserver(this)
            observeFavoritesList(this)
            observeFavorite(this)
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
            adapter = gifListAdapter
        }
        setupAlertDialog(view)
        setupSnackBar(view)
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

    private fun setupAlertDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.setView(layoutInflater.inflate(R.layout.custom_dialog, null))
        alertDialog = builder.create()
    }

    private fun setupSnackBar(view: View) {
        snackBar = Snackbar.make(
            view.fragment_favorite_content_holder,
            "",
            Snackbar.LENGTH_SHORT
        )
    }

    private fun observeFavoritesList(favoriteViewModel: FavoriteViewModel) {
        favoriteViewModel.liveDataFavoritesList.observe(viewLifecycleOwner, {
            when (it?.status) {
                ViewData.Status.LOADING -> {
                    fragment_favorite_progress_bar.visible()
                    fragment_favorite_recycler_view.gone()
                    fragment_favorite_text_view_empty.gone()
                }
                ViewData.Status.SUCCESS -> {
                    fragment_favorite_progress_bar.gone()
                    if (it.data.isNullOrEmpty()) {
                        fragment_favorite_recycler_view.gone()
                        fragment_favorite_text_view_empty.visible()
                    } else {
                        fragment_favorite_recycler_view.visible()
                        fragment_favorite_text_view_empty.gone()
                        gifListAdapter.submitList(
                            it.data
                        )
                    }
                }
                ViewData.Status.ERROR -> {
                    fragment_favorite_text_view_empty.gone()
                    fragment_favorite_progress_bar.gone()
                    fragment_favorite_recycler_view.gone()
                }
            }
        })
    }

    override fun layoutResource(): Int = R.layout.fragment_favorite

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