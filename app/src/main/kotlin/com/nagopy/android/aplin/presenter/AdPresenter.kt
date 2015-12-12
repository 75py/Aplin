/*
 * Copyright 2015 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nagopy.android.aplin.presenter

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.nagopy.android.aplin.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 広告表示（AdMob）の処理を移譲するためのクラス
 */
@Singleton
public open class AdPresenter : Presenter {

    @Inject
    constructor()

    var adView: AdView? = null

    open fun initialize(adView: AdView) {
        this.adView = adView
        val adRequest = AdRequest.Builder()
                .addTestDevice("2E21E51466C94A2960FCB4E0BB5DB388")
                .addTestDevice("561DC184323F7A23F20080805D44083C")
                .addTestDevice("0FDB3E1E20DE9A1E911A85F87903A069")
                .addTestDevice("F3D630FD4B16A430A0CB29123A096F71")
                .build()
        if (!BuildConfig.DEBUG && BuildConfig.PRODUCTION) {
            adView.loadAd(adRequest)
        }
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
