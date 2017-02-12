package com.nagopy.android.aplin.model

import android.content.pm.PackageManager
import android.os.Build
import com.nagopy.android.aplin.entity.PermissionGroup
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionGroups @Inject constructor() {

    @Inject
    lateinit var shellCmd: ShellCmd

    @Inject
    lateinit var packageManager: PackageManager

    fun getAllPermissionGroups(): List<PermissionGroup> {
        val commands =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // 24-
                    listOf("cmd", "package", "list", "permissions", "-g", "-d")
                } else {
                    // -23
                    listOf("pm", "list", "permissions", "-g", "-d")
                }

        var groupName: String? = null
        var permissions = ArrayList<String>()

        val result = ArrayList<PermissionGroup>()

        shellCmd.exec(commands, { seq: Sequence<String> ->
            seq.map(String::trim)
                    .filter(String::isNotEmpty)
                    .forEach {
                        if (it.startsWith("group:")) {
                            if (groupName != null) {
                                result.add(PermissionGroup(groupName!!, getGroupLabel(groupName!!), permissions))
                            }
                            // next group
                            groupName = it.replace("group:", "")
                            permissions = ArrayList()
                        } else if (it.startsWith("permission:")) {
                            permissions.add(it.replace("permission:", ""))
                        }
                    }
        }, Unit)

        if (groupName != null) {
            // last group
            result.add(PermissionGroup(groupName!!, getGroupLabel(groupName!!), permissions))
        }

        return result
    }

    private fun getGroupLabel(groupName: String): String {
        try {
            val pgi = packageManager.getPermissionGroupInfo(groupName, 0)
            return pgi.loadLabel(packageManager).toString()
        } catch (e: Exception) {
            Timber.w(e)
            return groupName.split(".").last()
        }
    }
}