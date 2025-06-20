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
    screen: Screen.AppListScreen,
    launcherLargeIconSize: Int,
    startDetailSettingsActivity: (String) -> Unit,
    searchByWeb: (PackageModel) -> Unit,
) {
    if (state.packagesModel == null) {
        Loading()
    } else {
        VerticalAppList(
            modifier = Modifier.padding(8.dp),
            packages = screen.getAppList(state.packagesModel, state.searchText),
            launcherLargeIconSize = launcherLargeIconSize,
            startDetailSettingsActivity = startDetailSettingsActivity,
            searchByWeb = searchByWeb,
        )
    }
}

@Preview(name = "5-inch Device Portrait", widthDp = 360, heightDp = 640)
@Preview(name = "5-inch Device Landscape", widthDp = 640, heightDp = 360)
@Preview(name = "10-inch Tablet Portrait", widthDp = 600, heightDp = 960)
@Preview(name = "10-inch Tablet Landscape", widthDp = 960, heightDp = 600)
@Composable
fun AppListScreenLoadedPreview() {
    val packages =
        IntRange(0, 20).map {
            PackageModel(
                packageName = "com.example$it",
                label = "Example Label $it",
                icon =
                    AppCompatResources.getDrawable(
                        LocalContext.current,
                        R.mipmap.ic_launcher,
                    )!!,
                isEnabled = it % 2 == 0,
                firstInstallTime = 0L,
                lastUpdateTime = 0L,
                versionName = "1.0.0",
            )
        }
    AppListScreen(
        state =
            MainUiState(
                isLoading = false,
                packagesModel = PackagesModel(packages, packages, packages, packages),
            ),
        screen = Screen.AllAppList,
        launcherLargeIconSize = 36,
        startDetailSettingsActivity = {},
        searchByWeb = {},
    )
}
