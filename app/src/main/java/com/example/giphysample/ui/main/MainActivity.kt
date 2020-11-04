package com.example.giphysample.ui.main

import android.os.Bundle
import com.example.giphysample.R
import com.example.giphysample.ui.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun layoutResource(): Int? = R.layout.activity_main

    override fun setupView() {
        setupToolbar()
        setupViewPager()
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.main_activity_toolbar))
    }

    private fun setupViewPager() {
        main_activity_view_pager?.let {
            it.adapter = SectionsPagerAdapter(lifecycle, supportFragmentManager)
            TabLayoutMediator(main_activity_tab_layout, it) { tab, position ->
                tab.text = getString(TAB_TITLES[position])
            }.attach()
        }
    }
}