/*
 * Copyright 2017 75py
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

import com.google.android.gms.ads.AdView
import org.junit.Before
import org.junit.Test
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AdPresenterTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var adView: AdView

    lateinit var adPresenter: AdPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        assertNotNull(adView)

        adPresenter = AdPresenter()
    }

    @Test
    fun initialize() {
        Mockito.doNothing().`when`(adView).loadAd(Mockito.any())

        adPresenter.showAds = true
        adPresenter.initialize(adView)
        assertNotNull(adPresenter.adView)
        assertEquals(adView, adPresenter.adView)
        Mockito.verify(adView, Mockito.times(1)).loadAd(Mockito.any())
    }


    @Test
    fun initialize_debug() {
        Mockito.doNothing().`when`(adView).loadAd(Mockito.any())

        adPresenter.showAds = false
        adPresenter.initialize(adView)
        assertNull(adPresenter.adView)
        Mockito.verify(adView, Mockito.times(0)).loadAd(Mockito.any())
    }

    @Test
    fun resume() {
        Mockito.doNothing().`when`(adView).resume()
        adPresenter.adView = adView
        adPresenter.showAds = true
        adPresenter.resume()

        Mockito.verify(adView, Mockito.times(1)).resume()
    }


    @Test
    fun resume_debug() {
        Mockito.doNothing().`when`(adView).resume()
        adPresenter.adView = null
        adPresenter.showAds = false
        adPresenter.resume()

        Mockito.verify(adView, Mockito.times(0)).resume()
    }

    @Test
    fun pause() {
        Mockito.doNothing().`when`(adView).pause()
        adPresenter.adView = adView
        adPresenter.showAds = true
        adPresenter.pause()

        Mockito.verify(adView, Mockito.times(1)).pause()
    }

    @Test
    fun pause_debug() {
        Mockito.doNothing().`when`(adView).pause()
        adPresenter.adView = null
        adPresenter.showAds = false
        adPresenter.pause()

        Mockito.verify(adView, Mockito.times(0)).pause()
    }

    @Test
    fun destroy() {
        Mockito.doNothing().`when`(adView).destroy()
        adPresenter.adView = adView
        adPresenter.showAds = true
        adPresenter.destroy()

        Mockito.verify(adView, Mockito.times(1)).destroy()
        assertNull(adPresenter.adView)
    }

    @Test
    fun destroy_debug() {
        Mockito.doNothing().`when`(adView).destroy()
        adPresenter.adView = null
        adPresenter.showAds = false
        adPresenter.destroy()

        Mockito.verify(adView, Mockito.times(0)).destroy()
    }
}
