package com.nagopy.android.aplin.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.ui.ads.AdsViewModel
import com.nagopy.android.aplin.ui.main.compose.RootScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModel()

    private val adsViewModel: AdsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state = mainViewModel.viewModelState.collectAsState().value
            RootScreen(
                state = state,
                mainViewModel = mainViewModel,
                startDetailSettingsActivity = { pkg: String ->
                    mainViewModel.startDetailSettingsActivity(
                        this@MainActivity,
                        pkg,
                    )
                },
                searchByWeb = { packageModel: PackageModel ->
                    mainViewModel.searchByWeb(this@MainActivity, packageModel)
                },
                sharePackages = { packages ->
                    mainViewModel.sharePackages(this@MainActivity, packages)
                },
                startLicensesActivity = {
                    mainViewModel.startLicensesActivity(this)
                },
                adsStatus = adsViewModel.adsState.collectAsState().value,
                privacyOptionsRequired = adsViewModel.privacyOptionsRequired.collectAsState().value,
                showPrivacyOptions = {
                    adsViewModel.showPrivacyOptions(this)
                },
            )
        }

        adsViewModel.initialize(this)
    }

    override fun onRestart() {
        super.onRestart()
        mainViewModel.updatePackages()
    }
}
