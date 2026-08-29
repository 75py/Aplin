package com.nagopy.android.aplin.ui.ads.compose

import android.content.res.Resources
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.nagopy.android.aplin.BuildConfig
import com.nagopy.android.aplin.ui.ads.AdsStatus

@Composable
fun AdBanner(adsStatus: AdsStatus) {
    if (adsStatus != AdsStatus.Ready) {
        return
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val adViewReference = remember { AdViewReference() }

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> adViewReference.value?.resume()
                    Lifecycle.Event.ON_PAUSE -> adViewReference.value?.pause()
                    else -> Unit
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                val displayMetrics = Resources.getSystem().displayMetrics
                val width = (displayMetrics.widthPixels / displayMetrics.density).toInt()
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width))
                adUnitId = BuildConfig.AD_UNIT_ID
                adViewReference.value = this
                loadAd(AdRequest.Builder().build())
            }
        },
        update = {},
        onRelease = { releasedAdView ->
            releasedAdView.pause()
            releasedAdView.destroy()
            if (adViewReference.value === releasedAdView) {
                adViewReference.value = null
            }
        },
    )
}

private class AdViewReference {
    var value: AdView? = null
}
