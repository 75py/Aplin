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

package com.nagopy.android.aplin.model

import android.app.Application
import android.os.Build
import android.support.test.InstrumentationRegistry
import android.test.suitebuilder.annotation.SmallTest
import com.nagopy.android.aplin.Aplin
import com.nagopy.android.aplin.ApplicationMockComponent
import com.nagopy.android.aplin.ApplicationMockModule
import com.nagopy.android.aplin.DaggerApplicationMockComponent
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import javax.inject.Inject
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@SmallTest
class AplinDevicePolicyManagerTest {

    val application = InstrumentationRegistry.getTargetContext().applicationContext as Application

    @Inject
    lateinit var aplinDevicePolicyManager: AplinDevicePolicyManager

    @Before
    fun setup() {
        Aplin.component = DaggerApplicationMockComponent.builder()
                .applicationMockModule(ApplicationMockModule(application))
                .build()

        (Aplin.getApplicationComponent() as ApplicationMockComponent).inject(this)
    }

    @Test
    fun reflectionEnabled() {
        assertNotNull(aplinDevicePolicyManager)
        assertNotNull(aplinDevicePolicyManager.application)
        assertNotNull(aplinDevicePolicyManager.devicePolicyManager)
        assertNotNull(aplinDevicePolicyManager.packageManager)
        assertNotNull(aplinDevicePolicyManager.packageHasActiveAdmins)
        assertNotNull(aplinDevicePolicyManager.mSystemPackageInfo)
    }

    @Test
    fun isProfileOrDeviceOwner() {
        aplinDevicePolicyManager.devicePolicyManager = Mockito.spy(aplinDevicePolicyManager.devicePolicyManager)
        Mockito.`when`(aplinDevicePolicyManager.devicePolicyManager.isDeviceOwnerApp("testpkg")).thenReturn(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Mockito.`when`(aplinDevicePolicyManager.devicePolicyManager.isProfileOwnerApp("testpkg")).thenReturn(false)
        }
        assertFalse(aplinDevicePolicyManager.isProfileOrDeviceOwner("testpkg"))

        Mockito.verify(aplinDevicePolicyManager.devicePolicyManager, Mockito.times(1)).isDeviceOwnerApp("testpkg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Mockito.verify(aplinDevicePolicyManager.devicePolicyManager, Mockito.times(1)).isProfileOwnerApp("testpkg")
        }
    }
}