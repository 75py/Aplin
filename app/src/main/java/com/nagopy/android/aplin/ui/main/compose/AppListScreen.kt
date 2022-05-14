package com.nagopy.android.aplin.ui.main.compose

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.domain.model.PackagesModel
import com.nagopy.android.aplin.ui.main.MainUiState
import com.nagopy.android.aplin.ui.main.Screen

@Composable
fun AppListScreen(
    state: MainUiState,
    screen: Screen.AppList,
    launcherLargeIconSize: Int,
    startDetailSettingsActivity: (String) -> Unit,
    searchByWeb: (PackageModel) -> Unit,
) {
    if (state.packagesModel == null) {
        Loading()
    } else {
        AppListScreenLoaded(
            packages = screen.getAppList(state.packagesModel, state.searchText),
            launcherLargeIconSize = launcherLargeIconSize,
            startDetailSettingsActivity = startDetailSettingsActivity,
            searchByWeb = searchByWeb,
        )
    }
}

@Composable
fun AppListScreenLoaded(
    packages: List<PackageModel>,
    launcherLargeIconSize: Int,
    startDetailSettingsActivity: (String) -> Unit,
    searchByWeb: (PackageModel) -> Unit,
) {
    VerticalAppList(
        modifier = Modifier.padding(8.dp),
        packages = packages,
        launcherLargeIconSize = launcherLargeIconSize,
        startDetailSettingsActivity = startDetailSettingsActivity,
        searchByWeb = searchByWeb,
    )
}

@Preview
@Composable
fun AppListScreenLoadedPreview() {
    val packages = IntRange(0, 20).map {
        PackageModel(
            packageName = "com.example$it",
            label = "Example Label $it",
            icon = AppCompatResources.getDrawable(
                LocalContext.current,
                R.mipmap.ic_launcher
            )!!,
            isEnabled = it % 2 == 0,
            firstInstallTime = 0L,
            lastUpdateTime = 0L,
            versionName = "1.0.0",
        )
    }
    AppListScreen(
        state = MainUiState(
            isLoading = false,
            packagesModel = PackagesModel(packages, packages, packages)
        ),
        screen = Screen.AllAppList,
        launcherLargeIconSize = 36,
        startDetailSettingsActivity = {},
        searchByWeb = {},
    )
}
