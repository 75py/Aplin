package com.nagopy.android.aplin.data.repository

import android.app.admin.DevicePolicyManager
import androidx.annotation.VisibleForTesting
import logcat.LogPriority
import logcat.logcat
import java.lang.reflect.Method

class DevicePolicyRepositoryImpl(
    private val devicePolicyManager: DevicePolicyManager
) : DevicePolicyRepository {

    @VisibleForTesting
    val packageHasActiveAdmins: Method? by lazy {
        try {
            DevicePolicyManager::class.java.getDeclaredMethod(
                "packageHasActiveAdmins",
                String::class.java
            )
        } catch (t: Throwable) {
            logcat(LogPriority.VERBOSE) { "packageHasActiveAdmins: $t" }
            null
        }
    }

    override fun packageHasActiveAdmins(packageName: String): Boolean {
        try {
            return packageHasActiveAdmins?.invoke(devicePolicyManager, packageName) as Boolean
        } catch (t: Throwable) {
            logcat(LogPriority.VERBOSE) { "packageHasActiveAdmins: $t" }
        }
        return false
    }

    override fun isProfileOrDeviceOwner(packageName: String): Boolean =
        devicePolicyManager.isDeviceOwnerApp(packageName) ||
            devicePolicyManager.isProfileOwnerApp(packageName)
}
