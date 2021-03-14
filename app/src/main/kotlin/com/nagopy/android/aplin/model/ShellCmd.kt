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

import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShellCmd @Inject constructor() {

    @Throws(Exception::class)
    fun <T> exec(commands: Collection<String>, useLines: (Sequence<String>) -> T): T {
        Timber.d("exec %s", commands)
        val pb = ProcessBuilder(commands.toList())
        val p = pb.start()
        val s = p.inputStream
        val isr = InputStreamReader(s)
        val br = BufferedReader(isr)
        return br.useLines(useLines)
    }

    fun <T> exec(commands: Collection<String>, useLines: (Sequence<String>) -> T, defaultValue: T): T {
        return try {
            exec(commands, useLines)
        } catch (e: Exception) {
            Timber.w(e)
            defaultValue
        }
    }
}
