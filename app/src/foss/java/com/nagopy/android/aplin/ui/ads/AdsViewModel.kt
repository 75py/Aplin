package com.nagopy.android.aplin.ui.ads

import android.app.Activity
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdsViewModel : ViewModel() {
    val adsState: StateFlow<AdsStatus> = MutableStateFlow(AdsStatus.NotAllowed).asStateFlow()
    val privacyOptionsRequired: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()

    fun initialize(activity: Activity) = Unit

    fun showPrivacyOptions(activity: Activity) = Unit
}
