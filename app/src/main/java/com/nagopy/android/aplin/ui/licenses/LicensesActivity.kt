package com.nagopy.android.aplin.ui.licenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.ui.theme.AplinTheme

class LicensesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val entries =
            runCatching { LicenseCatalogLoader(assets).load() }
                .getOrElse { emptyList() }
        setContent {
            AplinTheme {
                LicenseScreen(entries)
            }
        }
    }
}

@Composable
private fun LicenseScreen(entries: List<LicenseEntry>) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.background(MaterialTheme.colors.primarySurface).statusBarsPadding(),
                title = { Text(stringResource(R.string.licenses)) },
            )
        },
    ) { padding ->
        if (entries.isEmpty()) {
            Text(
                text = stringResource(R.string.licenses_unavailable),
                modifier = Modifier.padding(padding).navigationBarsPadding().padding(16.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).navigationBarsPadding(),
            ) {
                items(entries) { entry ->
                    LicenseRow(entry)
                }
            }
        }
    }
}

@Composable
private fun LicenseRow(entry: LicenseEntry) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(text = entry.coordinate, style = MaterialTheme.typography.subtitle1)
        if (entry.name.isNotBlank()) {
            Text(text = entry.name, style = MaterialTheme.typography.body2)
        }
        Text(text = entry.version, style = MaterialTheme.typography.body2)
        if (entry.licenses.isEmpty()) {
            Text(text = stringResource(R.string.license_unknown), style = MaterialTheme.typography.body2)
        } else {
            entry.licenses.forEach { license ->
                val licenseLabel =
                    listOf(license.name, license.identifier)
                        .filter(String::isNotBlank)
                        .joinToString(" / ")
                        .ifBlank { stringResource(R.string.license_unknown) }
                Text(text = licenseLabel, style = MaterialTheme.typography.body2)
                if (license.url.isNotBlank()) {
                    Text(text = license.url, style = MaterialTheme.typography.body2)
                }
            }
        }
        if (entry.sourceUrl.isNotBlank()) {
            Text(
                text = stringResource(R.string.license_source, entry.sourceUrl),
                style = MaterialTheme.typography.body2,
            )
        }
    }
}
