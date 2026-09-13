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

class LicenseNoticesLoader(
    private val assets: AssetManager,
) {
    fun load(): List<String> {
        val notices =
            assets
                .open(ASSET_PATH)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
        return LicenseNoticeChunker.chunk(notices)
    }

    companion object {
        const val ASSET_PATH = "licenses/third-party-notices.txt"
    }
}

internal object LicenseNoticeChunker {
    private const val MAX_CHUNK_SIZE = 8_000
    private const val PREFERRED_BOUNDARY_MINIMUM = MAX_CHUNK_SIZE / 2

    fun chunk(text: String): List<String> {
        if (text.isBlank()) {
            return emptyList()
        }
        val chunks = mutableListOf<String>()
        var start = 0
        while (start < text.length) {
            val end = findChunkEnd(text, start)
            chunks += text.substring(start, end)
            start = end
        }
        return chunks
    }

    private fun findChunkEnd(
        text: String,
        start: Int,
    ): Int {
        val limit = minOf(start + MAX_CHUNK_SIZE, text.length)
        if (limit == text.length) {
            return limit
        }
        val safeLimit = limit - if (text[limit - 1].isHighSurrogate() && text[limit].isLowSurrogate()) 1 else 0
        val preferredStart = start + PREFERRED_BOUNDARY_MINIMUM
        val paragraphBoundary = text.lastIndexOf("\n\n", startIndex = safeLimit - 2)
        if (paragraphBoundary >= preferredStart) {
            return paragraphBoundary + 2
        }
        val lineBoundary = text.lastIndexOf('\n', startIndex = safeLimit - 1)
        if (lineBoundary >= preferredStart) {
            return lineBoundary + 1
        }
        return safeLimit
    }
}
