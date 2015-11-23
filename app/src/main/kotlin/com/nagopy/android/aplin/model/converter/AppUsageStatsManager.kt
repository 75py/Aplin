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

package com.nagopy.android.aplin.model.converter

import android.annotation.TargetApi
import android.app.Application
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import com.nagopy.android.aplin.forEachX
import java.util.*
import javax.inject.Inject

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class AppUsageStatsManager
@Inject
constructor() {

    @Inject
    lateinit var application: Application

    var cache: Map<String, Int>? = null

    @Synchronized fun getLaunchTimes(): Map<String, Int> {
        if (cache != null) {
            return cache!!
        }

        val usageStatsManager: UsageStatsManager = application.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        val end = calendar.timeInMillis
        calendar.add(Calendar.YEAR, -1)
        val start = calendar.timeInMillis
        val stats = usageStatsManager.queryEvents(start, end)


        val countMap = HashMap<String, Int>()
        val events = ArrayList<UsageEvents.Event>()
        while (stats.hasNextEvent()) {
            val e = UsageEvents.Event()
            if (stats.getNextEvent(e)) {
                events.add(e)
            }
        }

        events.forEachX({ first -> /* skip */ }, { current, previous ->
            if (previous.packageName == current.packageName
                    && previous.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND
                    && current.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND
                    && current.timeStamp - previous.timeStamp > 3000) {
                val count = countMap[current.packageName] ?: 0
                countMap.put(current.packageName, count + 1)
            }
        })

        cache = countMap

        return cache!!
    }
}

