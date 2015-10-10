package com.nagopy.android.aplin.presenter

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 広告表示（AdMob）の処理を移譲するためのクラス
 */
@Singleton
public class AdPresenter : Presenter {

    @Inject
    constructor()

    var adView: AdView? = null

    public fun initialize(adView: AdView) {
        this.adView = adView
        val adRequest = AdRequest.Builder()
                .addTestDevice("2E21E51466C94A2960FCB4E0BB5DB388")
                .addTestDevice("561DC184323F7A23F20080805D44083C")
                .build()
        adView.loadAd(adRequest)
    }

    public override fun resume() {
        adView?.resume()
    }

    public override fun pause() {
        adView?.pause()
    }

    public override fun destroy() {
        adView?.destroy()
        adView = null
    }
}
