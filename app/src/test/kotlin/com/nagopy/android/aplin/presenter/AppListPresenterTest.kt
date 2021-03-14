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
import android.os.Build
import android.view.MenuItem
import com.nagopy.android.aplin.BuildConfig
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.Applications
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.model.IconHelper
import com.nagopy.android.aplin.model.UserSettings
import com.nagopy.android.aplin.view.AppListView
import com.nagopy.android.aplin.view.AppListViewParent
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AppListPresenterTest {

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

    @InjectMocks
    lateinit var appListPresenter: AppListPresenter

    @Mock
    lateinit var item: MenuItem

    val mockList = ArrayList<App>().apply {
        add(App().apply {
            packageName = BuildConfig.APPLICATION_ID
        })
    }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        `when`(applications.getApplicationList(Category.ALL)).thenReturn(Single.create { it.onSuccess(mockList) })
        appListPresenter.defList = mockList
        appListPresenter.filteredList.addAll(mockList)
    }

    @Test
    fun initialize() {
        callInitialize()

        assertEquals(appListView, appListPresenter.view)
        assertEquals(appListViewParent, appListPresenter.parentView)
        assertEquals(Category.ALL, appListPresenter.category)
        verify(applications, times(1)).getApplicationList(Category.ALL)
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
    }

    @Test
    fun onOptionsItemSelected() {
        callInitialize()

        appListPresenter.onOptionsItemSelected(item)
        verify(appListViewParent, times(1))
                .onOptionsItemSelected(item, mockList)
    }

    @Test
    fun onItemClicked() {
        callInitialize()

        appListPresenter.onItemClicked(0)
        verify(appListViewParent, times(1))
                .onListItemClicked(mockList[0], Category.ALL)
    }


    @Test
    fun onItemLongClicked() {
        callInitialize()

        appListPresenter.onItemLongClicked(0)
        verify(appListViewParent, times(1))
                .onListItemLongClicked(mockList[0])
    }

}
