package com.nagopy.android.aplin.ui.main.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.nagopy.android.aplin.ui.main.SearchWidgetState
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
                MainAppBar(
                    navController = navController,
                    state = state,
                    currentScreen = currentScreen,
                    sharePackages = sharePackages,
                    onTextChanged = {
                        mainViewModel.updateSearchTextState(it)
                    },
                    onCloseClicked = {
                        mainViewModel.updateSearchTextState("")
                        mainViewModel.updateSearchWidgetState(SearchWidgetState.CLOSED)
                    },
                    onSearchTriggered = {
                        mainViewModel.updateSearchWidgetState(SearchWidgetState.OPENED)
                    },
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
                    Screen.appListScreens.forEach { appListScreen ->
                        composable(appListScreen.route) {
                            AppListScreen(
                                state = state,
                                screen = appListScreen,
                                launcherLargeIconSize = mainViewModel.launcherLargeIconSize,
                                startDetailSettingsActivity = startDetailSettingsActivity,
                                searchByWeb = searchByWeb,
                            )
                        }
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
