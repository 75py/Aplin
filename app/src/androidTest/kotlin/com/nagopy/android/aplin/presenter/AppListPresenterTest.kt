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
import android.test.suitebuilder.annotation.SmallTest
import android.widget.ImageView
import com.nagopy.android.aplin.model.Applications
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.model.IconHelper
import com.nagopy.android.aplin.model.UserSettings
import com.nagopy.android.aplin.view.AppListView
import com.nagopy.android.aplin.view.AppListViewParent
import com.nagopy.android.aplin.view.adapter.AppListAdapter
import org.junit.Before
import org.junit.Test
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SmallTest
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

    lateinit var appListPresenter: AppListPresenter

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var holder: AppListAdapter.ViewHolder

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
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

        // Mockにして close() が呼ばれたか、にしたい
        // assertTrue(appListPresenter.realm.isClosed)
    }

    @Test
    fun onAttachedToRecyclerView() {
        // RealmResultsをモックにできないので不可
    }

    @Test
    fun onDetachedFromRecyclerView() {
        // RealmResultsをモックにできないので不可
    }

    @Test
    fun onCreateViewHolder() {
        Mockito.`when`(iconHelper.iconSize).thenReturn(999)

        callInitialize()
        appListPresenter.onCreateViewHolder(holder)

        Mockito.verify(holder.parent, Mockito.times(1)).setOnClickListener(Mockito.any())
        Mockito.verify(holder.parent, Mockito.times(1)).setOnLongClickListener(Mockito.any())
        Mockito.verify(holder.icon, Mockito.times(1)).scaleType = ImageView.ScaleType.FIT_CENTER
        assertEquals(999, holder.icon.layoutParams.width)
        assertEquals(999, holder.icon.layoutParams.height)
    }

    @Test
    fun onBindViewHolder() {
        // ここが肝心だけど、RealmResultsがMockにできないと……
    }

    @Test
    fun getItemCount() {
        // RealmResultsをMockにしないとできない
    }

    @Test
    fun onOptionsItemSelected() {
        // RealmResultsをMockにしないとできない
    }

}
