package com.nagopy.android.aplin.ui.ads

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import logcat.logcat
import java.lang.ref.WeakReference

class AdsViewModel : ViewModel() {
    private val _adsState = MutableStateFlow(AdsStatus.NotInitialized)
    val adsState: StateFlow<AdsStatus> = _adsState.asStateFlow()

    private val _privacyOptionsRequired = MutableStateFlow(false)
    val privacyOptionsRequired: StateFlow<Boolean> = _privacyOptionsRequired.asStateFlow()

    private var mobileAdsInitializationState = MobileAdsInitializationState.NotStarted
    private var consentInformation: ConsentInformation? = null
    private var consentUpdateRequested = false
    private var umpOperationInProgress = false
    private var latestActivity: WeakReference<Activity>? = null

    fun initialize(activity: Activity) {
        latestActivity = WeakReference(activity)
        val information =
            consentInformation
                ?: UserMessagingPlatform.getConsentInformation(activity).also {
                    consentInformation = it
                }
        updatePrivacyOptionsRequired(information)
        if (consentUpdateRequested) {
            if (!umpOperationInProgress) {
                reconcileAdsState(information)
            }
            return
        }
        consentUpdateRequested = true
        umpOperationInProgress = true
        val params = ConsentRequestParameters.Builder().build()

        information.requestConsentInfoUpdate(
            activity,
            params,
            {
                updatePrivacyOptionsRequired(information)
                val currentActivity = latestActivity?.get()
                if (currentActivity == null || !currentActivity.isUsable()) {
                    finishUmpOperation(information)
                } else {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(currentActivity) { error ->
                        error?.let { logcat { "Consent form error: $it" } }
                        finishUmpOperation(information)
                    }
                }
            },
            { error ->
                logcat { "Consent info update error: $error" }
                // UMP retains the previous session's valid consent. It may still allow ads.
                updatePrivacyOptionsRequired(information)
                finishUmpOperation(information, errorState = true)
            },
        )
    }

    fun showPrivacyOptions(activity: Activity) {
        latestActivity = WeakReference(activity)
        val information = consentInformation ?: return
        if (umpOperationInProgress) {
            return
        }
        if (information.privacyOptionsRequirementStatus !=
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        ) {
            return
        }
        umpOperationInProgress = true
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { error ->
            error?.let { logcat { "Privacy options form error: $it" } }
            finishUmpOperation(information, errorState = error != null)
        }
    }

    private fun updatePrivacyOptionsRequired(information: ConsentInformation) {
        _privacyOptionsRequired.value =
            information.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    private fun reconcileAdsState(
        information: ConsentInformation,
        errorState: Boolean = false,
    ) {
        if (!information.canRequestAds()) {
            _adsState.value = if (errorState) AdsStatus.Error else AdsStatus.NotAllowed
            return
        }
        when (mobileAdsInitializationState) {
            MobileAdsInitializationState.NotStarted -> initializeMobileAdsIfNeeded()
            MobileAdsInitializationState.InProgress -> _adsState.value = AdsStatus.NotInitialized
            MobileAdsInitializationState.Initialized -> _adsState.value = AdsStatus.Ready
        }
    }

    private fun finishUmpOperation(
        information: ConsentInformation,
        errorState: Boolean = false,
    ) {
        if (!umpOperationInProgress) {
            return
        }
        updatePrivacyOptionsRequired(information)
        umpOperationInProgress = false
        reconcileAdsState(information, errorState)
    }

    private fun initializeMobileAdsIfNeeded() {
        if (mobileAdsInitializationState != MobileAdsInitializationState.NotStarted) {
            return
        }
        val context = latestActivity?.get()?.applicationContext ?: return
        mobileAdsInitializationState = MobileAdsInitializationState.InProgress
        MobileAds.initialize(context) {
            mobileAdsInitializationState = MobileAdsInitializationState.Initialized
            consentInformation?.let { information ->
                reconcileAdsState(information)
            }
        }
    }

    private fun Activity.isUsable(): Boolean = !isFinishing && !isDestroyed

    override fun onCleared() {
        latestActivity = null
        super.onCleared()
    }

    private enum class MobileAdsInitializationState {
        NotStarted,
        InProgress,
        Initialized,
    }
}
