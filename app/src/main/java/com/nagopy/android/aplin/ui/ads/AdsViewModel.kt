package com.nagopy.android.aplin.ui.ads

import android.app.Activity
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.nagopy.android.aplin.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import logcat.LogPriority
import logcat.logcat

class AdsViewModel(
    private val prefs: SharedPreferences,
) : ViewModel() {

    private val _adsState = MutableStateFlow(AdsStatus.NotInitialized)
    val adsState =
        _adsState.stateIn(viewModelScope, SharingStarted.Eagerly, _adsState.value)

    private val _isGDPRState = MutableStateFlow(false)
    val isGDPRState =
        _isGDPRState.stateIn(viewModelScope, SharingStarted.Eagerly, _isGDPRState.value)

    private lateinit var consentInformation: ConsentInformation

    private fun updateState() {
        _adsState.update {
            if (isGDPR()) {
                when {
                    canShowPersonalizedAds() -> {
                        AdsStatus.Personalized
                    }
                    canShowAds() -> {
                        AdsStatus.NonPersonalized
                    }
                    else -> {
                        AdsStatus.Denied
                    }
                }
            } else {
                AdsStatus.Personalized
            }
        }
    }

    private fun updateGDPRState() {
        val isGDPR = isGDPR()
        _isGDPRState.update { isGDPR }
    }

    fun init(activity: Activity) {
        // Set tag for underage of consent. false means users are not underage.
        val params = ConsentRequestParameters.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    setConsentDebugSettings(
                        ConsentDebugSettings.Builder(activity)
                            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                            // .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA)
                            .build()
                    )
                }
            }
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        printLogs("showConsentFormIfNeeded 1")

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            { // The consent information state was updated.
                printLogs("requestConsentInfoUpdate")
                updateGDPRState()
                updateState()

                // You are now ready to check if a form is available.
                if (consentInformation.isConsentFormAvailable) {
                    loadForm(activity)
                }
            },
            {
                // Handle the error.
                logcat { "$it" }
                printLogs("requestConsentInfoUpdate error")
                updateGDPRState()
                _adsState.update { AdsStatus.Error }
            }
        )
    }

    fun loadForm(activity: Activity, force: Boolean = false) {
        UserMessagingPlatform.loadConsentForm(
            activity,
            { consentForm ->
                printLogs("loadConsentForm")
                updateState()
                if (force || consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    consentForm.show(activity) {
                        // Handle dismissal by reloading form.
                        logcat { "consentForm.show FormError=$it" }
                        loadForm(activity)
                    }
                }
            }
        ) {
            // / Handle Error.
            logcat { "$it" }
            printLogs("loadConsentForm error")
            _adsState.update { AdsStatus.Error }
        }
    }

    private fun isGDPR(): Boolean {
        val gdpr = prefs.getInt("IABTCF_gdprApplies", 0)
        return gdpr == 1
    }

    private fun canShowAds(): Boolean {
        // https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20CMP%20API%20v2.md#in-app-details
        // https://support.google.com/admob/answer/9760862?hl=en&ref_topic=9756841

        val purposeConsent = prefs.getString("IABTCF_PurposeConsents", "") ?: ""
        val vendorConsent = prefs.getString("IABTCF_VendorConsents", "") ?: ""
        val vendorLI = prefs.getString("IABTCF_VendorLegitimateInterests", "") ?: ""
        val purposeLI = prefs.getString("IABTCF_PurposeLegitimateInterests", "") ?: ""

        val googleId = 755
        val hasGoogleVendorConsent = hasAttribute(vendorConsent, index = googleId)
        val hasGoogleVendorLI = hasAttribute(vendorLI, index = googleId)

        // Minimum required for at least non-personalized ads
        return hasConsentFor(listOf(1), purposeConsent, hasGoogleVendorConsent) &&
            hasConsentOrLegitimateInterestFor(
                listOf(2, 7, 9, 10),
                purposeConsent,
                purposeLI,
                hasGoogleVendorConsent,
                hasGoogleVendorLI
            )
    }

    private fun canShowPersonalizedAds(): Boolean {
        // https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20CMP%20API%20v2.md#in-app-details
        // https://support.google.com/admob/answer/9760862?hl=en&ref_topic=9756841

        val purposeConsent = prefs.getString("IABTCF_PurposeConsents", "") ?: ""
        val vendorConsent = prefs.getString("IABTCF_VendorConsents", "") ?: ""
        val vendorLI = prefs.getString("IABTCF_VendorLegitimateInterests", "") ?: ""
        val purposeLI = prefs.getString("IABTCF_PurposeLegitimateInterests", "") ?: ""

        val googleId = 755
        val hasGoogleVendorConsent = hasAttribute(vendorConsent, index = googleId)
        val hasGoogleVendorLI = hasAttribute(vendorLI, index = googleId)

        return hasConsentFor(listOf(1, 3, 4), purposeConsent, hasGoogleVendorConsent) &&
            hasConsentOrLegitimateInterestFor(
                listOf(2, 7, 9, 10),
                purposeConsent,
                purposeLI,
                hasGoogleVendorConsent,
                hasGoogleVendorLI
            )
    }

    // Check if a binary string has a "1" at position "index" (1-based)
    private fun hasAttribute(input: String, index: Int): Boolean {
        return input.length >= index && input[index - 1] == '1'
    }

    // Check if consent is given for a list of purposes
    private fun hasConsentFor(
        purposes: List<Int>,
        purposeConsent: String,
        hasVendorConsent: Boolean
    ): Boolean {
        return purposes.all { p -> hasAttribute(purposeConsent, p) } && hasVendorConsent
    }

    // Check if a vendor either has consent or legitimate interest for a list of purposes
    private fun hasConsentOrLegitimateInterestFor(
        purposes: List<Int>,
        purposeConsent: String,
        purposeLI: String,
        hasVendorConsent: Boolean,
        hasVendorLI: Boolean
    ): Boolean {
        return purposes.all { p ->
            (hasAttribute(purposeLI, p) && hasVendorLI) ||
                (hasAttribute(purposeConsent, p) && hasVendorConsent)
        }
    }

    private fun printLogs(msg: String) {
        logcat(LogPriority.VERBOSE) {
            """
----------------------
msg: $msg
isGDPR: ${isGDPR()}
canShowAds: ${canShowAds()}
canShowPersonalizedAds: ${canShowPersonalizedAds()}
consentStatus: ${consentInformation.consentStatus}
isConsentFormAvailable: ${consentInformation.isConsentFormAvailable}
----------------------
            """
        }
    }
}
