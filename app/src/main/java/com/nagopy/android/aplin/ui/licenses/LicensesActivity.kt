package com.nagopy.android.aplin.ui.licenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.ui.theme.AplinTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LicensesActivity : ComponentActivity() {
    private var showNotices by mutableStateOf(false)
    private var noticesState by mutableStateOf<NoticesState>(NoticesState.Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        showNotices = savedInstanceState?.getBoolean(SHOW_NOTICES_KEY) == true
        val entries =
            runCatching { LicenseCatalogLoader(assets).load() }
                .getOrElse { emptyList() }
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (showNotices) {
                        showNotices = false
                    } else {
                        finish()
                    }
                }
            },
        )
        if (showNotices) {
            loadNotices()
        }
        setContent {
            AplinTheme {
                if (showNotices) {
                    LicenseNoticesScreen(
                        state = noticesState,
                        onBack = { showNotices = false },
                    )
                } else {
                    LicenseScreen(
                        entries = entries,
                        onOpenNotices = ::openNotices,
                    )
                }
            }
        }
    }

    private fun openNotices() {
        showNotices = true
        loadNotices()
    }

    private fun loadNotices() {
        noticesState = NoticesState.Loading
        lifecycleScope.launch {
            noticesState =
                runCatching {
                    withContext(Dispatchers.IO) {
                        LicenseNoticesLoader(assets).load()
                    }
                }.fold(
                    onSuccess = { chunks ->
                        if (chunks.isEmpty()) {
                            NoticesState.Unavailable
                        } else {
                            NoticesState.Content(chunks)
                        }
                    },
                    onFailure = { NoticesState.Unavailable },
                )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(SHOW_NOTICES_KEY, showNotices)
        super.onSaveInstanceState(outState)
    }

    private companion object {
        const val SHOW_NOTICES_KEY = "show_license_notices"
    }
}

@Composable
private fun LicenseScreen(
    entries: List<LicenseEntry>,
    onOpenNotices: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.background(MaterialTheme.colors.primarySurface).statusBarsPadding(),
                title = { Text(stringResource(R.string.licenses)) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).navigationBarsPadding(),
        ) {
            Button(
                onClick = onOpenNotices,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(stringResource(R.string.licenses_notices_button))
            }
            if (entries.isEmpty()) {
                Text(
                    text = stringResource(R.string.licenses_unavailable),
                    modifier = Modifier.padding(16.dp),
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(entries) { entry ->
                        LicenseRow(entry)
                    }
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

private sealed interface NoticesState {
    data object Loading : NoticesState

    data class Content(
        val chunks: List<String>,
    ) : NoticesState

    data object Unavailable : NoticesState
}

@Composable
private fun LicenseNoticesScreen(
    state: NoticesState,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.background(MaterialTheme.colors.primarySurface).statusBarsPadding(),
                title = { Text(stringResource(R.string.licenses_notices_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        when (state) {
            NoticesState.Loading ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
                    Text(
                        text = stringResource(R.string.licenses_notices_loading),
                        modifier = Modifier.padding(16.dp),
                    )
                }

            NoticesState.Unavailable ->
                Text(
                    text = stringResource(R.string.licenses_notices_unavailable),
                    modifier = Modifier.padding(padding).navigationBarsPadding().padding(16.dp),
                )

            is NoticesState.Content -> {
                if (state.chunks.isEmpty()) {
                    Text(
                        text = stringResource(R.string.licenses_notices_unavailable),
                        modifier = Modifier.padding(padding).navigationBarsPadding().padding(16.dp),
                    )
                } else {
                    SelectionContainer {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(padding).navigationBarsPadding(),
                        ) {
                            items(state.chunks) { chunk ->
                                Text(
                                    text = chunk,
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    style = MaterialTheme.typography.body1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
