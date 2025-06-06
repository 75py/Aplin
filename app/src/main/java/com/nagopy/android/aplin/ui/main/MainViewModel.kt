package com.nagopy.android.aplin.ui.main

import android.app.Activity
import android.app.ActivityManager
import android.app.SearchManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ShareCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.domain.usecase.LoadPackagesUseCase
import com.nagopy.android.aplin.ui.prefs.UserDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.logcat
import java.net.URLEncoder

class MainViewModel(
    activityManager: ActivityManager,
    private val packageManager: PackageManager,
    private val loadPackagesUseCase: LoadPackagesUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val userDataStore: UserDataStore,
) : ViewModel() {
    private val _viewModelState = MutableStateFlow(MainUiState(isLoading = false))
    val viewModelState =
        _viewModelState.stateIn(viewModelScope, SharingStarted.Eagerly, _viewModelState.value)

    val launcherLargeIconSize = activityManager.launcherLargeIconSize

    init {
        updatePackages()

        viewModelScope.launch(ioDispatcher) {
            userDataStore.sortOrder.collect { newOrder ->
                logcat { "sortOrder: $newOrder" }
                _viewModelState.update {
                    it.copy(
                        isLoading = false,
                        packagesModel =
                            it.packagesModel?.let { packagesModel ->
                                newOrder.sort(
                                    packagesModel,
                                )
                            },
                        sortOrder = newOrder,
                    )
                }
            }
        }
    }

    fun updatePackages() {
        if (_viewModelState.value.isLoading) {
            return
        }
        _viewModelState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch(ioDispatcher) {
            val result = loadPackagesUseCase.execute()
            _viewModelState.update {
                it.copy(
                    isLoading = false,
                    packagesModel = it.sortOrder.sort(result),
                )
            }
        }
    }

    fun startDetailSettingsActivity(
        activity: Activity,
        pkg: String,
    ) {
        val packageName = pkg.split(":")[0]
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        if (activity.isInMultiWindowMode) {
            intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        try {
            activity.startActivity(intent)
        } catch (e: Exception) {
            logcat { "$e" }
        }
    }

    fun searchByWeb(
        activity: Activity,
        packageModel: PackageModel,
    ) {
        val actionWebSearch =
            Intent(Intent.ACTION_WEB_SEARCH)
                .putExtra(SearchManager.QUERY, "${packageModel.label} ${packageModel.packageName}")

        if (isLaunchable(actionWebSearch)) {
            activity.startActivity(actionWebSearch)
        } else {
            val url = "https://www.google.com/search?q=${
                URLEncoder.encode(
                    packageModel.label,
                    "UTF-8",
                )
            }%20${packageModel.packageName}"
            val actionView =
                Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(url))
            if (isLaunchable(actionView)) {
                activity.startActivity(actionView)
            } else {
                logcat { "ActivityNotFound" }
            }
        }
    }

    private fun isLaunchable(intent: Intent): Boolean {
        return intent.resolveActivity(packageManager) != null
    }

    fun startOssLicensesActivity(activity: Activity) {
        activity.startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
    }

    fun sharePackages(
        activity: Activity,
        packages: List<PackageModel>,
    ) {
        ShareCompat.IntentBuilder(activity)
            .setText(packages.joinToString(separator = LINE_SEPARATOR) { it.packageName })
            .setType("text/plain")
            .startChooser()
    }

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _viewModelState.update {
            it.copy(searchWidgetState = newValue)
        }
    }

    fun updateSearchTextState(newValue: String) {
        _viewModelState.update {
            it.copy(searchText = newValue)
        }
    }

    companion object {
        private val LINE_SEPARATOR: String = System.getProperty("line.separator") ?: "\n"
    }
}
