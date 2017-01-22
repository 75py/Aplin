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

import android.app.Application
import android.content.Context
import android.os.Build
import com.nagopy.android.aplin.BuildConfig
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.Applications
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.model.IconHelper
import com.nagopy.android.aplin.model.UserSettings
import com.nagopy.android.aplin.view.AppListView
import com.nagopy.android.aplin.view.AppListViewParent
import com.nagopy.android.aplin.view.adapter.AppListAdapter
import io.realm.*
import io.realm.internal.RealmCore
import io.realm.log.RealmLog
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Matchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.*
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor
import org.powermock.modules.junit4.rule.PowerMockRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class
        , sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP)
        //        , manifest = "src/main/AndroidManifest.xml"
)
@PowerMockIgnore("org.mockito.*", "org.robolectric.*", "android.*", "org.json.*")
@PrepareForTest(Realm::class, RealmConfiguration::class, RealmQuery::class, RealmResults::class, RealmCore::class, RealmLog::class)
@SuppressStaticInitializationFor("io.realm.internal.Util")
class AppListPresenterTest {

    @Rule
    @JvmField
    val rule = PowerMockRule()

    @Mock
    lateinit var application: Application

    @Mock
    lateinit var userSettings: UserSettings

    @Mock
    lateinit var applications: Applications

    @Mock
    lateinit var iconHelper: IconHelper

    @Mock
    lateinit var appListView: AppListView

    @Mock
    lateinit var appListViewParent: AppListViewParent

    lateinit var appListPresenter: AppListPresenter

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var holder: AppListAdapter.ViewHolder

    @Before
    fun setup() {
        // Setup Realm to be mocked. The order of these matters
        mockStatic(RealmCore::class.java)
        mockStatic(RealmLog::class.java)
        mockStatic(Realm::class.java)
        mockStatic(RealmConfiguration::class.java)
        Realm.init(RuntimeEnvironment.application);

        // Create the mock
        val mockRealm = PowerMockito.mock(Realm::class.java)
        val mockRealmConfig = mock(RealmConfiguration::class.java)

        doNothing().`when`(RealmCore::class.java)
        RealmCore.loadLibrary(any(Context::class.java))

        whenNew(RealmConfiguration::class.java).withAnyArguments().thenReturn(mockRealmConfig)

        `when`(Realm.getDefaultInstance()).thenReturn(mockRealm)


        MockitoAnnotations.initMocks(this)
        val list = ArrayList<App>()
        val mockResult: RealmResults<App> = MockRealm.mockRealmResult(mockRealm, list)
        Mockito.`when`(applications.getApplicationList(Category.ALL)).thenReturn(mockResult)
        PowerMockito.doNothing().`when`(mockRealm).close()

        appListPresenter = AppListPresenter()
        appListPresenter.application = application
        appListPresenter.applications = applications
        appListPresenter.userSettings = userSettings
        appListPresenter.iconHelper = iconHelper
    }

    @Test
    fun initialize() {
        callInitialize()

        assertNotNull(appListPresenter.realm)
        assertEquals(appListView, appListPresenter.view)
        assertEquals(appListViewParent, appListPresenter.parentView)
        assertEquals(Category.ALL, appListPresenter.category)
        Mockito.verify(applications, Mockito.times(1)).getApplicationList(Category.ALL)
    }

    private fun callInitialize() {
        appListPresenter.initialize(appListView, appListViewParent, Category.ALL)
    }

    @Test
    fun resume() {
        appListPresenter.resume()
    }

    @Test
    fun pause() {
        appListPresenter.pause()
    }

    @Test
    fun destroy() {
        callInitialize()
        appListPresenter.destroy()

        assertNull(appListPresenter.view)
        assertNull(appListPresenter.parentView)

        PowerMockito.verifyPrivate(appListPresenter.realm, Mockito.times(1)).invoke("close")
    }

}
