package com.nagopy.android.aplin.ui.ads.compose

import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.nagopy.android.aplin.BuildConfig
import com.nagopy.android.aplin.ui.ads.AdsStatus

@Composable
fun AdBanner(
    state: AdsStatus,
    updateAds: (AdsStatus, AdView) -> Unit,
) {
    if (state != AdsStatus.Personalized && state != AdsStatus.NonPersonalized) {
        return
    }

    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(Color.Red)
        )
    } else {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                val adView = AdView(context)
                val displayMetrics = Resources.getSystem().displayMetrics
                val width = (displayMetrics.widthPixels / displayMetrics.density).toInt()
                adView.adSize =
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width)
                adView.adUnitId = BuildConfig.AD_UNIT_ID
                adView
            },
            update = { updateAds.invoke(state, it) }
        )
    }
}
