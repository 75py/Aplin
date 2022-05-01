package com.nagopy.android.aplin.ui.main.compose

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.domain.model.PackagesModel
import com.nagopy.android.aplin.ui.main.AppCategory
import com.nagopy.android.aplin.ui.main.MainUiState

@Composable
fun AppListScreen(
    state: MainUiState,
    appCategory: AppCategory,
    launcherLargeIconSize: Int,
    startDetailSettingsActivity: (String) -> Unit,
    sharePackages: (List<PackageModel>) -> Unit,
) {
    if (state.packagesModel == null) {
        Loading()
    } else {
        AppListScreenLoaded(
            appCategory = appCategory,
            packagesModel = state.packagesModel,
            launcherLargeIconSize = launcherLargeIconSize,
            startDetailSettingsActivity = startDetailSettingsActivity,
            sharePackages = sharePackages,
        )
    }
}

@Composable
fun AppListScreenLoaded(
    appCategory: AppCategory,
    packagesModel: PackagesModel,
    launcherLargeIconSize: Int,
    startDetailSettingsActivity: (String) -> Unit,
    sharePackages: (List<PackageModel>) -> Unit,
) {
    val packages = when (appCategory) {
        AppCategory.USERS -> packagesModel.userPackages
        AppCategory.DISABLEABLE -> packagesModel.disableablePackages
        AppCategory.ALL -> packagesModel.allPackages
    }

    Column {
        Surface(elevation = 3.dp) {
            Row(
                modifier = Modifier
                    .clickable {
                        sharePackages.invoke(packages)
                    }
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(id = appCategory.labelId) + " (${packages.size})",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_share_24),
                    contentDescription = stringResource(id = R.string.share),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        VerticalAppList(
            modifier = Modifier
                .padding(8.dp)
                .weight(1.0f),
            packages = packages,
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
    AppListScreen(
        state = MainUiState(
            isLoading = false,
            packagesModel = PackagesModel(packages, packages, packages)
        ),
        appCategory = AppCategory.ALL,
        launcherLargeIconSize = 36,
        startDetailSettingsActivity = {},
        sharePackages = {},
    )
}
