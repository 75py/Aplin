package com.nagopy.android.aplin.ui.main.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nagopy.android.aplin.domain.model.PackageModel

@Composable
fun HorizontalAppSection(
    title: String,
    packages: List<PackageModel>,
    navigateToVerticalList: () -> Unit,
    startDetailSettingsActivity: (String) -> Unit,
    searchByWeb: (PackageModel) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .clickable {
                    navigateToVerticalList.invoke()
                }
                .padding(8.dp)
        ) {
            Text(
                text = title + " (${packages.size})",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = ""
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalAppList(packages, startDetailSettingsActivity, searchByWeb)
    }
}
