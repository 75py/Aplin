package com.nagopy.android.aplin

import android.support.test.InstrumentationRegistry
import android.support.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class NoAnimationTestRule : TestRule {

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
                val backupAnimationScale = getAnimationScale(uiDevice)
                putAnimationScale(uiDevice, disableAnimationScale)
                try {
                    base?.evaluate()
                } finally {
                    putAnimationScale(uiDevice, backupAnimationScale)
                }
            }
        }
    }

    data class AnimationScale(
            val windowAnimationScale: String
            , val transitionAnimationScale: String
            , val animatorDurationScale: String
    )

    val disableAnimationScale = AnimationScale("0.0", "0.0", "0.0")

    fun getAnimationScale(uiDevice: UiDevice): AnimationScale? {
        return try {
            val windowAnimationScale = uiDevice.executeShellCommand("settings get global window_animation_scale")
            val transitionAnimationScale = uiDevice.executeShellCommand("settings get global transition_animation_scale")
            val animatorDurationScale = uiDevice.executeShellCommand("settings get global animator_duration_scale")
            AnimationScale(windowAnimationScale, transitionAnimationScale, animatorDurationScale)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun putAnimationScale(uiDevice: UiDevice, animationScale: AnimationScale?) {
        if (animationScale != null) {
            try {
                uiDevice.executeShellCommand("settings put global window_animation_scale ${animationScale.windowAnimationScale}")
                uiDevice.executeShellCommand("settings put global transition_animation_scale ${animationScale.transitionAnimationScale}")
                uiDevice.executeShellCommand("settings put global animator_duration_scale ${animationScale.animatorDurationScale}")
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

}
