package com.nagopy.android.aplin

import android.arch.lifecycle.ViewModelProviders
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.nagopy.android.aplin.databinding.ActivityMainBinding
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    val injector = KodeinInjector()

    lateinit var binding: ActivityMainBinding

    val mainViewModelFactory: MainViewModel.Factory by injector.instance()

    val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(this, mainViewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(appKodein())

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolBar)
        binding.mainViewModel = mainViewModel

        launch(UI) {
            Timber.d("start loading")

            async(CommonPool) {
                mainViewModel.loadApplications()
            }.await()

            binding.viewPager.adapter = AppListPagerAdapter(supportFragmentManager, resources)
            binding.tabLayout.setupWithViewPager(binding.viewPager)
            Timber.d("finish loading")
        }

        Timber.d("onCreate end")
    }

    class AppListPagerAdapter(fm: FragmentManager, val resources: Resources) : FragmentPagerAdapter(fm) {

        val categories = Category.values()

        override fun getItem(position: Int): Fragment {
            return AppListFragment.newInstance(categories[position])
        }

        override fun getCount(): Int {
            return categories.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return resources.getText(categories[position].titleResId)
        }
    }

}