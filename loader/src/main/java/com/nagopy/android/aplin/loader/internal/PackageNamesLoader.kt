package com.nagopy.android.aplin.loader.internal

internal class PackageNamesLoader(val shellCmd: ShellCmd) {

    fun getInstalledPackageNames(): List<String> {
        return shellCmd.exec(listOf("cmd", "package", "list", "packages"), { seq ->
            seq.filter(String::isNotBlank)
                    .filter { it.startsWith("package:") }
                    .map { it.replace("package:", "") }
                    .toList()
        }, emptyList())
    }

}
