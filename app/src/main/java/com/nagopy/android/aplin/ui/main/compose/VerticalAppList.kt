package com.nagopy.android.aplin.ui.main.compose

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.domain.model.PackageModel

@Composable
fun VerticalAppList(
    modifier: Modifier = Modifier,
    packages: List<PackageModel>,
    launcherLargeIconSize: Int,
    startDetailSettingsActivity: (String) -> Unit,
) {
    val iconSize = with(LocalDensity.current) { launcherLargeIconSize.toDp() }
    LazyColumn(modifier) {
        items(packages) { pkg ->
            Item(startDetailSettingsActivity, iconSize, pkg)
        }
    }
}

@Composable
private fun Item(
    startDetailSettingsActivity: (String) -> Unit,
    iconSize: Dp,
    pkg: PackageModel,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .alpha(if (pkg.isEnabled) 1.0f else 0.5f)
            .clickable {
                startDetailSettingsActivity.invoke(pkg.packageName)
            },
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
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
                )
                Text(
                    text = pkg.packageName,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Preview
@Composable
fun ItemPreview() {
    Item(
        startDetailSettingsActivity = {}, iconSize = 32.dp,
        pkg = PackageModel(
            packageName = "com.example",
            label = "Example Label",
            icon = AppCompatResources.getDrawable(
                LocalContext.current,
                R.mipmap.ic_launcher
            )!!,
            isEnabled = true,
            firstInstallTime = 0L,
            lastUpdateTime = 0L,
            versionName = "1.0.0",
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
            versionName = "1.0.0",
        )
    }
    VerticalAppList(
        packages = packages,
        launcherLargeIconSize = 32,
        startDetailSettingsActivity = {}
    )
}
