package com.nagopy.android.aplin.ui.main.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdView
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.ui.ads.AdsStatus
import com.nagopy.android.aplin.ui.ads.compose.AdBanner
import com.nagopy.android.aplin.ui.main.MainUiState
import com.nagopy.android.aplin.ui.main.MainViewModel
import com.nagopy.android.aplin.ui.main.Screen
import com.nagopy.android.aplin.ui.theme.AplinTheme

@Composable
fun RootScreen(
    state: MainUiState,
    mainViewModel: MainViewModel,
    startDetailSettingsActivity: (String) -> Unit,
    searchByWeb: (PackageModel) -> Unit,
    sharePackages: (List<PackageModel>) -> Unit,
    startOssLicensesActivity: () -> Unit,
    adsStatus: AdsStatus,
    isGDPR: Boolean,
    showConsentForm: () -> Unit,
    updateAds: (AdsStatus, AdView) -> Unit,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.find(navBackStackEntry?.destination?.route)
    AplinTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = currentScreen.resourceId))
                    },
                    actions = {
                        if (state.isLoading && state.packagesModel != null) {
                            CircularProgressIndicator(color = Color.LightGray)
                        }
                        if (currentScreen is Screen.AppList && state.packagesModel != null) {
                            IconButton(onClick = {
                                sharePackages.invoke(
                                    currentScreen.getAppList(
                                        state.packagesModel
                                    )
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share"
                                )
                            }
                        }
                    },
                    navigationIcon = if (currentScreen != Screen.Top) {
                        {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    } else {
                        null
                    }
                )
            }
        ) {
            Column(Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Top.route,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(Screen.Top.route) {
                        MainScreen(
                            navController = navController,
                            state = state,
                            startDetailSettingsActivity = startDetailSettingsActivity,
                            searchByWeb = searchByWeb,
                            startOssLicensesActivity = startOssLicensesActivity,
                            isGDPR = isGDPR,
                            showConsentForm = showConsentForm,
                        )
                    }
                    composable(Screen.UserAppList.route) {
                        AppListScreen(
                            state = state,
                            screen = Screen.UserAppList,
                            launcherLargeIconSize = mainViewModel.launcherLargeIconSize,
                            startDetailSettingsActivity = startDetailSettingsActivity,
                            searchByWeb = searchByWeb,
                        )
                    }
                    composable(Screen.DisableableAppList.route) {
                        AppListScreen(
                            state = state,
                            screen = Screen.DisableableAppList,
                            launcherLargeIconSize = mainViewModel.launcherLargeIconSize,
                            startDetailSettingsActivity = startDetailSettingsActivity,
                            searchByWeb = searchByWeb,
                        )
                    }
                    composable(Screen.AllAppList.route) {
                        AppListScreen(
                            state = state,
                            screen = Screen.AllAppList,
                            launcherLargeIconSize = mainViewModel.launcherLargeIconSize,
                            startDetailSettingsActivity = startDetailSettingsActivity,
                            searchByWeb = searchByWeb,
                        )
                    }
                }

                AdBanner(
                    state = adsStatus,
                    updateAds = updateAds,
                )
            }
        }
    }
}
