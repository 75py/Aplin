package com.nagopy.android.aplin.domain.usecase

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.nagopy.android.aplin.data.repository.DevicePolicyRepositoryImpl
import com.nagopy.android.aplin.data.repository.PackageRepositoryImpl
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadPackagesUseCaseTest {

    @Test
    fun test() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val useCase = LoadPackagesUseCase(
            PackageRepositoryImpl(context.packageManager),
            CategorizePackageUseCase(
                PackageRepositoryImpl(context.packageManager),
                DevicePolicyRepositoryImpl(context.getSystemService(DevicePolicyManager::class.java))
            )
        )

        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val timeout = 3000L

        uiDevice.pressHome()

        runBlocking {
            val result = useCase.execute()
            result.disableablePackages.forEach { model ->
                // sleep対策
                uiDevice.pressBack()
                uiDevice.wait(
                    Until.hasObject(By.pkg(uiDevice.launcherPackageName).depth(0)),
                    timeout
                )

                startDetailSettingsActivity(context, model.packageName)
                uiDevice.wait(
                    Until.hasObject(By.pkg("com.android.settings").depth(0)),
                    timeout
                )

                val isLabelFound =
                    uiDevice.findObject(UiSelector().text(model.label)).waitForExists(timeout)
                if (!isLabelFound) {
                    logcat(LogPriority.ERROR) { "label not found: ${model.packageName} ${model.label}" }
                    return@forEach
                }

                val disableButtonLabel =
                    InstrumentationRegistry.getInstrumentation().context.getString(
                        if (model.isEnabled) {
                            com.nagopy.android.aplin.test.R.string.test_btn_enable
                        } else {
                            com.nagopy.android.aplin.test.R.string.test_btn_disable
                        }
                    )

                val isDisableButtonFound =
                    uiDevice.findObject(UiSelector().textStartsWith(disableButtonLabel))
                        .waitForExists(timeout)
                if (!isDisableButtonFound) {
                    logcat(LogPriority.ERROR) { "disable button not found: ${model.packageName} ${model.label}" }
                    return@forEach
                }

                val isDisableButtonEnabled =
                    uiDevice.findObject(UiSelector().textStartsWith(disableButtonLabel)).isEnabled
                if (!isDisableButtonEnabled) {
                    logcat(LogPriority.ERROR) { "disable button is not enabled: ${model.packageName} ${model.label}" }
                    return@forEach
                }
            }
        }
    }

    private fun startDetailSettingsActivity(context: Context, pkg: String) {
        val packageName = pkg.split(":")[0]
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }
}
