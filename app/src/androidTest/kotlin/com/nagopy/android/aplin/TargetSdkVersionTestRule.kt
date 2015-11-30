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

package com.nagopy.android.aplin

import android.os.Build
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TargetSdkVersionTestRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return AplinStatement(base, description)
    }

    class AplinStatement(val base: Statement, val description: Description) : Statement() {
        override fun evaluate() {
            val annotation = description.getAnnotation(TargetSdkVersion::class.java)
            if (annotation != null) {
                val minSdkVersion = annotation.minSdkVersion
                if (Build.VERSION.SDK_INT < minSdkVersion
                        || annotation.maxSdkVersion < Build.VERSION.SDK_INT) {
                    println("Skipped")
                    return
                }
            }
            base.evaluate()
        }

    }

    // 書く順番が大事？
    // @Test
    // @TargetSdkVersion
    // なら取得できる。逆だとできない。
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION)
    annotation class TargetSdkVersion(val minSdkVersion: Int = Build.VERSION_CODES.BASE
                                      , val maxSdkVersion: Int = 999)
}
