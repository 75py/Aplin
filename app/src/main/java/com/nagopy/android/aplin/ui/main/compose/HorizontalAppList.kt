package com.nagopy.android.aplin.ui.main.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.nagopy.android.aplin.domain.model.PackageModel
import kotlin.math.min

@Composable
fun HorizontalAppList(
    disableablePackages: List<PackageModel>,
    startDetailSettingsActivity: (String) -> Unit,
    searchByWeb: (PackageModel) -> Unit
) {
    val lc = LocalConfiguration.current
    val itemWidth = remember {
        min(lc.screenWidthDp, lc.screenHeightDp).dp / 2.6f
    }
    val iconSize = remember {
        itemWidth / 2.0f
    }
    LazyRow {
        items(disableablePackages) { pkg ->
            Item(itemWidth, iconSize, startDetailSettingsActivity, searchByWeb, pkg)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Item(
    itemWidth: Dp,
    iconSize: Dp,
    startDetailSettingsActivity: (String) -> Unit,
    searchByWeb: (PackageModel) -> Unit,
    pkg: PackageModel
) {
    Card(
        modifier = Modifier
            .width(itemWidth)
            .wrapContentHeight()
            .padding(8.dp)
            .alpha(if (pkg.isEnabled) 1.0f else 0.5f)
            .combinedClickable(
                onClick = { startDetailSettingsActivity.invoke(pkg.packageName) },
                onLongClick = { searchByWeb.invoke(pkg) }
            )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberDrawablePainter(drawable = pkg.icon),
                contentDescription = "",
                modifier = Modifier.size(iconSize)
            )
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = pkg.label,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = pkg.packageName,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
