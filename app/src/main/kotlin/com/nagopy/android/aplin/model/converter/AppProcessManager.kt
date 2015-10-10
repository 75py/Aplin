package com.nagopy.android.aplin.model.converter

import android.app.ActivityManager
import android.app.Application
import com.nagopy.android.aplin.constants.Constants
import java.util.*
import javax.inject.Inject

class AppProcessManager
@Inject
constructor() {

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var activityManager: ActivityManager

    @Synchronized fun getRunningStatusMap(): Map<String, List<String>> {
        val runningAppProcessInfoList = activityManager.runningAppProcesses

        val runningStatusMap = HashMap<String, MutableList<String>>()
        for (runningAppProcessInfo in runningAppProcessInfoList) {
            val pkgList = runningAppProcessInfo.pkgList
            for (pkg in pkgList) {
                var list = runningStatusMap.get(pkg) ?: ArrayList<String>()
                if (pkg == runningAppProcessInfo.processName) {
                    list.add("[${Constants.RUNNING_STATUS.get(runningAppProcessInfo.importance)}]")
                } else {
                    list.add("${runningAppProcessInfo.processName} [${Constants.RUNNING_STATUS.get(runningAppProcessInfo.importance)}]")
                }
                runningStatusMap.put(pkg, list)
            }
        }
        return runningStatusMap
    }


}
