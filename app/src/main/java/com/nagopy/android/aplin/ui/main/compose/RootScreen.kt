package com.nagopy.android.aplin.ui.main.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.ui.ads.AdsStatus
import com.nagopy.android.aplin.ui.ads.compose.AdBanner
import com.nagopy.android.aplin.ui.main.AppCategory
import com.nagopy.android.aplin.ui.main.MainUiState
import com.nagopy.android.aplin.ui.main.MainViewModel
import com.nagopy.android.aplin.ui.theme.AplinTheme

@Composable
fun RootScreen(
    state: MainUiState,
    mainViewModel: MainViewModel,
    startDetailSettingsActivity: (String) -> Unit,
    startOssLicensesActivity: () -> Unit,
    adsStatus: AdsStatus,
    isGDPR: Boolean,
    showConsentForm: () -> Unit,
) {
    val navController = rememberNavController()
    AplinTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    },
                    actions = {
                        if (state.isLoading && state.packagesModel != null) {
                            CircularProgressIndicator(color = Color.LightGray)
                        }
                    }
                )
            }
        ) {
            Column(Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = "main",
                    modifier = Modifier.weight(1f)
                ) {
                    composable("main") {
                        MainScreen(
                            navController = navController,
                            state = state,
                            startDetailSettingsActivity = startDetailSettingsActivity,
                            startOssLicensesActivity = startOssLicensesActivity,
                            isGDPR = isGDPR,
                            showConsentForm = showConsentForm,
                        )
                    }
                    composable("userAppList") {
                        AppListScreen(
                            state = state,
                            appCategory = AppCategory.USERS,
                            launcherLargeIconSize = mainViewModel.launcherLargeIconSize,
                            startDetailSettingsActivity = startDetailSettingsActivity,
                        )
                    }
                    composable("disableableAppList") {
                        AppListScreen(
                            state = state,
                            appCategory = AppCategory.DISABLEABLE,
                            launcherLargeIconSize = mainViewModel.launcherLargeIconSize,
                            startDetailSettingsActivity = startDetailSettingsActivity,
                        )
                    }
                    composable("allAppList") {
                        AppListScreen(
                            state = state,
                            appCategory = AppCategory.ALL,
                            launcherLargeIconSize = mainViewModel.launcherLargeIconSize,
                            startDetailSettingsActivity = startDetailSettingsActivity,
                        )
                    }
                }

                AdBanner(adsStatus)
            }
        }
    }
}