package com.nagopy.android.aplin.ui.licenses

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.nagopy.android.aplin.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class LicenseNoticesScreenTest {
    @Test
    fun licenseCatalogAndNoticesAreUsable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val uiDevice = UiDevice.getInstance(instrumentation)
        val catalogTitle = context.getString(R.string.licenses)
        val noticesButton = context.getString(R.string.licenses_notices_button)
        val noticesTitle = context.getString(R.string.licenses_notices_title)
        val expectedNoticeHeader =
            if (context.packageName.contains(".foss")) {
                "Aplin — FOSS release third-party licenses and notices"
            } else {
                "Aplin — PLAY release third-party licenses and notices"
            }

        val bundledNoticeHeader =
            context.assets
                .open(LicenseNoticesLoader.ASSET_PATH)
                .bufferedReader()
                .use { it.readLine() }
        assertEquals(expectedNoticeHeader, bundledNoticeHeader)

        context.startActivity(
            Intent(context, LicensesActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
        try {
            assertTextIsVisible(uiDevice, catalogTitle)
            assertTextIsVisible(uiDevice, noticesButton)
            takeScreenshot(context, uiDevice, "license-catalog.png")

            uiDevice.findObject(By.text(noticesButton)).click()
            assertTextIsVisible(uiDevice, noticesTitle)
            assertTextStartsWithIsVisible(uiDevice, expectedNoticeHeader)
            takeScreenshot(context, uiDevice, "license-notices.png")

            val noticesList = uiDevice.findObject(By.scrollable(true))
            assertNotNull("Full notices list should be scrollable", noticesList)
            val contentBounds = noticesList!!.visibleBounds
            // Compare only the body, excluding system bars, scrollbars, and overscroll effects.
            contentBounds.inset(contentBounds.width() / 10, contentBounds.height() / 10)
            uiDevice.waitForIdle()
            val screenshotBeforeScroll = takeContentScreenshot(uiDevice, contentBounds)
            try {
                // The return value reports whether more scrolling is possible, not whether content moved.
                noticesList.scroll(Direction.DOWN, 1.0f)
                uiDevice.waitForIdle()
                val screenshotAfterScroll = takeContentScreenshot(uiDevice, contentBounds)
                try {
                    assertTrue(
                        "Scrolling should change the visible notice content",
                        !screenshotBeforeScroll.sameAs(screenshotAfterScroll),
                    )
                } finally {
                    screenshotAfterScroll.recycle()
                }
            } finally {
                screenshotBeforeScroll.recycle()
            }

            uiDevice.pressBack()
            assertTextIsVisible(uiDevice, catalogTitle)
            assertTextIsVisible(uiDevice, noticesButton)
        } finally {
            closeLicenseActivity(uiDevice, context.packageName)
        }
    }

    private fun assertTextIsVisible(
        uiDevice: UiDevice,
        text: String,
    ) {
        assertTrue(
            "Expected visible text: $text",
            uiDevice.wait(Until.hasObject(By.text(text)), WAIT_TIMEOUT_MS),
        )
        assertNotNull(uiDevice.findObject(By.text(text)))
    }

    private fun assertTextStartsWithIsVisible(
        uiDevice: UiDevice,
        text: String,
    ) {
        val selector = By.textStartsWith(text)
        assertTrue(
            "Expected visible text starting with: $text",
            uiDevice.wait(Until.hasObject(selector), WAIT_TIMEOUT_MS),
        )
        assertNotNull(uiDevice.findObject(selector))
    }

    private fun takeScreenshot(
        context: Context,
        uiDevice: UiDevice,
        name: String,
    ) {
        val directory = File(requireNotNull(context.getExternalFilesDir(null)), SCREENSHOT_DIRECTORY)
        assertTrue("Could not create screenshot directory: $directory", directory.isDirectory || directory.mkdirs())
        val screenshot = File(directory, name)
        assertTrue("Could not capture screenshot: $screenshot", uiDevice.takeScreenshot(screenshot))
        assertTrue("Screenshot was not written: $screenshot", screenshot.isFile && screenshot.length() > 0)
    }

    private fun takeContentScreenshot(
        uiDevice: UiDevice,
        bounds: Rect,
    ): Bitmap {
        val screenshot = requireNotNull(uiDevice.takeScreenshot())
        val content = Bitmap.createBitmap(screenshot, bounds.left, bounds.top, bounds.width(), bounds.height())
        if (content !== screenshot) {
            screenshot.recycle()
        }
        return content
    }

    private fun closeLicenseActivity(
        uiDevice: UiDevice,
        packageName: String,
    ) {
        repeat(MAX_ACTIVITY_BACK_PRESSES) {
            if (uiDevice.findObject(By.pkg(packageName)) == null) {
                return
            }
            uiDevice.pressBack()
            uiDevice.wait(Until.gone(By.pkg(packageName)), SHORT_TIMEOUT_MS)
        }
    }

    private companion object {
        const val MAX_ACTIVITY_BACK_PRESSES = 2
        const val SCREENSHOT_DIRECTORY = "pr360"
        const val SHORT_TIMEOUT_MS = 1_000L
        const val WAIT_TIMEOUT_MS = 10_000L
    }
}
