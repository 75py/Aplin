package com.nagopy.android.aplin.ui.licenses

data class LicenseEntry(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val name: String,
    val licenses: List<LicenseReference>,
    val sourceUrl: String,
) {
    val coordinate: String
        get() = "$groupId:$artifactId:$version"
}

data class LicenseReference(
    val name: String,
    val identifier: String,
    val url: String,
)
