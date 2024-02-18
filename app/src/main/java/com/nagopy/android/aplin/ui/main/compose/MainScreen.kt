package com.nagopy.android.aplin.ui.main.compose

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.domain.model.PackagesModel
import com.nagopy.android.aplin.ui.main.MainUiState
import com.nagopy.android.aplin.ui.main.Screen
import com.nagopy.android.aplin.ui.theme.AplinTheme

@Composable
fun MainScreen(
    navController: NavController,
    state: MainUiState,
    startDetailSettingsActivity: (String) -> Unit,
    searchByWeb: (PackageModel) -> Unit,
    startOssLicensesActivity: () -> Unit,
    isGDPR: Boolean,
    showConsentForm: () -> Unit
) {
    if (state.packagesModel == null) {
        Loading()
    } else {
        MainScreenLoaded(
            navController,
            state.packagesModel,
            state.searchText,
            startDetailSettingsActivity,
            searchByWeb,
            startOssLicensesActivity,
            isGDPR,
            showConsentForm
        )
    }
}

@Composable
fun MainScreenLoaded(
    navController: NavController,
    packagesModel: PackagesModel,
    searchText: String,
    startDetailSettingsActivity: (String) -> Unit,
    searchByWeb: (PackageModel) -> Unit,
    startOssLicensesActivity: () -> Unit,
    isGDPR: Boolean,
    showConsentForm: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Screen.appListScreens.forEach { appListScreen ->
            HorizontalAppSection(
                title = stringResource(id = appListScreen.resourceId),
                packages = appListScreen.getAppList(packagesModel, searchText),
                navigateToVerticalList = {
                    navController.navigate(appListScreen.route)
                },
                startDetailSettingsActivity = startDetailSettingsActivity,
                searchByWeb = searchByWeb
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(onClick = startOssLicensesActivity) {
            Text(stringResource(id = R.string.licenses))
        }

        if (isGDPR) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = showConsentForm) {
                Text(stringResource(id = R.string.change_consent_state))
            }
        }
    }
}

@Preview(name = "5-inch Device Portrait", widthDp = 360, heightDp = 640)
@Preview(name = "5-inch Device Landscape", widthDp = 640, heightDp = 360)
@Preview(name = "10-inch Tablet Portrait", widthDp = 600, heightDp = 960)
@Preview(name = "10-inch Tablet Landscape", widthDp = 960, heightDp = 600)
@Composable
fun MainScreenLoadedPreview() {
    val packages = IntRange(0, 20).map {
        PackageModel(
            packageName = if (it % 2 == 0) "com.example.foo.bar.foo.bar$it" else "com.example",
            label = "Example Label $it",
            icon = AppCompatResources.getDrawable(
                LocalContext.current,
                R.mipmap.ic_launcher
            )!!,
            isEnabled = it % 2 == 0,
            firstInstallTime = 0L,
            lastUpdateTime = 0L,
            versionName = "1.0.0"
        )
    }
    AplinTheme {
        MainScreenLoaded(
            navController = rememberNavController(),
            packagesModel = PackagesModel(packages, packages, packages, packages),
            searchText = "",
            startOssLicensesActivity = {},
            searchByWeb = {},
            startDetailSettingsActivity = {},
            isGDPR = true,
            showConsentForm = {}
        )
    }
}
