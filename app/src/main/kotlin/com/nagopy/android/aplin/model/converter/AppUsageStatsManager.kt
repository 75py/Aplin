package com.nagopy.android.aplin.model.converter

import android.annotation.TargetApi
import android.app.Application
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.nagopy.android.aplin.constants.VersionCode
import com.nagopy.android.aplin.forEachX
import java.util.*
import javax.inject.Inject

@TargetApi(VersionCode.LOLLIPOP)
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
                val count = countMap.get(current.packageName) ?: 0
                countMap.put(current.packageName, count + 1)
            }
        })

        cache = countMap

        return cache!!
    }
}

