package com.nagopy.android.aplin.ui.licenses

import android.content.res.AssetManager

class LicenseCatalogLoader(
    private val assets: AssetManager,
) {
    fun load(): List<LicenseEntry> =
        assets
            .open(ASSET_PATH)
            .bufferedReader()
            .use { LicenseCatalogParser().parse(it.readText()) }

    companion object {
        const val ASSET_PATH = "app/cash/licensee/artifacts.json"
    }
}
