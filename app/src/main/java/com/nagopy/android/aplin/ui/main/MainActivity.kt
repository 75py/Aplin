package com.nagopy.android.aplin.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.nagopy.android.aplin.ui.ads.AdsViewModel
import com.nagopy.android.aplin.ui.main.compose.RootScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    private val adsViewModel: AdsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state = mainViewModel.viewModelState.collectAsState().value
            RootScreen(
                state = state,
                mainViewModel = mainViewModel,
                startDetailSettingsActivity = { pkg: String ->
                    mainViewModel.startDetailSettingsActivity(
                        this@MainActivity,
                        pkg
                    )
                },
                startOssLicensesActivity = {
                    mainViewModel.startOssLicensesActivity(this)
                },
                adsStatus = adsViewModel.adsState.collectAsState().value,
                isGDPR = adsViewModel.isGDPRState.collectAsState().value,
                showConsentForm = {
                    adsViewModel.loadForm(this, force = true)
                }
            )
        }

        adsViewModel.init(this)
    }

    override fun onRestart() {
        super.onRestart()
        mainViewModel.updatePackages()
    }
}
