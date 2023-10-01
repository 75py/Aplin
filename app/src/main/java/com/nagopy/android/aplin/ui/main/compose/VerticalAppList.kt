package com.nagopy.android.aplin.ui.main.compose

import android.text.format.DateFormat
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.ui.prefs.DisplayItem
import com.nagopy.android.aplin.ui.prefs.UserDataStore
import com.nagopy.android.aplin.ui.prefs.dataStore
import java.util.Date

private const val TOO_OLD_TIMESTAMP = 1230768000000L // 2009-01-01

@Composable
fun VerticalAppList(
    modifier: Modifier = Modifier,
    packages: List<PackageModel>,
    launcherLargeIconSize: Int,
    startDetailSettingsActivity: (String) -> Unit,
    searchByWeb: (PackageModel) -> Unit
) {
    val displayItems =
        UserDataStore(LocalContext.current.dataStore).displayItems.collectAsState(initial = emptyList())
    val iconSize = with(LocalDensity.current) { launcherLargeIconSize.toDp() }
    LazyColumn(modifier) {
        items(packages) { pkg ->
            Item(startDetailSettingsActivity, searchByWeb, iconSize, displayItems.value, pkg)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Item(
    startDetailSettingsActivity: (String) -> Unit,
    searchByWeb: (PackageModel) -> Unit,
    iconSize: Dp,
    displayItems: List<DisplayItem>,
    pkg: PackageModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .alpha(if (pkg.isEnabled) 1.0f else 0.5f)
            .combinedClickable(
                onClick = { startDetailSettingsActivity.invoke(pkg.packageName) },
                onLongClick = { searchByWeb.invoke(pkg) }
            )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberDrawablePainter(drawable = pkg.icon),
                contentDescription = "",
                modifier = Modifier.size(iconSize)
            )
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = pkg.label,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                if (displayItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (displayItems.contains(DisplayItem.FirstInstallTime) && pkg.firstInstallTime > TOO_OLD_TIMESTAMP) {
                    Text(
                        text = stringResource(
                            id = R.string.first_install_time_format,
                            DateFormat.format("yyyy/MM/dd kk:mm", pkg.firstInstallTime)
                        ),
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (displayItems.contains(DisplayItem.LastUpdateTime) && pkg.lastUpdateTime > TOO_OLD_TIMESTAMP) {
                    Text(
                        text = stringResource(
                            id = R.string.last_update_time_format,
                            DateFormat.format("yyyy/MM/dd kk:mm", pkg.lastUpdateTime)
                        ),
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (displayItems.contains(DisplayItem.VersionName) && !pkg.versionName.isNullOrEmpty()) {
                    Text(
                        text = stringResource(
                            id = R.string.version_format,
                            pkg.versionName
                        ),
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = pkg.packageName,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun ItemPreview() {
    Item(
        startDetailSettingsActivity = {},
        searchByWeb = {},
        iconSize = 32.dp,
        displayItems = DisplayItem.values().asList(),
        pkg = PackageModel(
            packageName = "com.example",
            label = "Example Label",
            icon = AppCompatResources.getDrawable(
                LocalContext.current,
                R.mipmap.ic_launcher
            )!!,
            isEnabled = true,
            firstInstallTime = Date().time,
            lastUpdateTime = Date().time,
            versionName = "1.0.0"
        )
    )
}

@Preview
@Composable
fun VerticalAppListPreview() {
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
            versionName = "1.0.0"
        )
    }
    VerticalAppList(
        packages = packages,
        launcherLargeIconSize = 32,
        startDetailSettingsActivity = {},
        searchByWeb = {}
    )
}
