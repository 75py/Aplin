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
import com.nagopy.android.aplin.ui.main.AppCategory
import com.nagopy.android.aplin.ui.main.MainUiState
import com.nagopy.android.aplin.ui.theme.AplinTheme

@Composable
fun AppListScreen(
    state: MainUiState,
    appCategory: AppCategory,
    launcherLargeIconSize: Int,
    startDetailSettingsActivity: (String) -> Unit,
) {
    if (state.packagesModel == null) {
        Loading()
    } else {
        AppListScreenLoaded(
            appCategory = appCategory,
            packagesModel = state.packagesModel,
            launcherLargeIconSize = launcherLargeIconSize,
            startDetailSettingsActivity = startDetailSettingsActivity,
        )
    }
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
    AplinTheme {
        AppListScreenLoaded(
            appCategory = AppCategory.ALL,
            packagesModel = PackagesModel(packages, packages, packages),
            launcherLargeIconSize = 36,
            startDetailSettingsActivity = {},
        )
    }
}

@Composable
fun AppListScreenLoaded(
    appCategory: AppCategory,
    packagesModel: PackagesModel,
    launcherLargeIconSize: Int,
    startDetailSettingsActivity: (String) -> Unit,
) {
    VerticalAppList(
        modifier = Modifier.padding(8.dp),
        packages = when (appCategory) {
            AppCategory.USERS -> packagesModel.userPackages
            AppCategory.DISABLEABLE -> packagesModel.disableablePackages
            AppCategory.ALL -> packagesModel.allPackages
        },
        launcherLargeIconSize = launcherLargeIconSize,
        startDetailSettingsActivity = startDetailSettingsActivity,
    )
}
