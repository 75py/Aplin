package com.nagopy.android.aplin.ui.main

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.nagopy.android.aplin.domain.model.PackagesModel
import com.nagopy.android.aplin.domain.usecase.LoadPackagesUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.logcat

class MainViewModel(
    activityManager: ActivityManager,
    private val loadPackagesUseCase: LoadPackagesUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _viewModelState = MutableStateFlow(MainUiState(isLoading = false))
    val viewModelState =
        _viewModelState.stateIn(viewModelScope, SharingStarted.Eagerly, _viewModelState.value)

    val launcherLargeIconSize = activityManager.launcherLargeIconSize

    init {
        updatePackages()
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
                    packagesModel = PackagesModel(
                        result.disableablePackages,
                        result.userPackages,
                        result.allPackages
                    )
                )
            }
        }
    }

    fun startDetailSettingsActivity(activity: Activity, pkg: String) {
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

    fun startOssLicensesActivity(activity: Activity) {
        activity.startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
    }
}
