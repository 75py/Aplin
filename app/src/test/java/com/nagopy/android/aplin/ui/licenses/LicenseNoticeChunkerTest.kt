package com.nagopy.android.aplin.ui.licenses

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LicenseNoticeChunkerTest {
    @Test
    fun preservesNoticeContentAcrossChunks() {
        val notice = "A".repeat(7_500) + "\n\n" + "B".repeat(9_000)

        val chunks = LicenseNoticeChunker.chunk(notice)

        assertEquals(notice, chunks.joinToString(separator = ""))
    }

    @Test
    fun keepsChunksWithinMaximumSize() {
        val notice = "notice ".repeat(3_000)

        val chunks = LicenseNoticeChunker.chunk(notice)

        assertTrue(chunks.isNotEmpty())
        assertTrue(chunks.all { it.length <= 8_000 })
    }

    @Test
    fun prefersParagraphAndLineBoundaries() {
        val paragraphNotice = "A".repeat(4_000) + "\n\n" + "B".repeat(8_000)
        val lineNotice = "A".repeat(4_500) + "\n" + "B".repeat(8_000)

        val paragraphChunks = LicenseNoticeChunker.chunk(paragraphNotice)
        val lineChunks = LicenseNoticeChunker.chunk(lineNotice)

        assertTrue(paragraphChunks.first().endsWith("\n\n"))
        assertTrue(lineChunks.first().endsWith("\n"))
    }

    @Test
    fun doesNotSplitSurrogatePairAtFallbackBoundary() {
        val notice = "A".repeat(7_999) + "😀" + "B".repeat(20)

        val chunks = LicenseNoticeChunker.chunk(notice)

        assertEquals(notice, chunks.joinToString(separator = ""))
        val firstChunkLastCharacter = chunks.first().last()
        val secondChunkFirstCharacter = chunks[1].first()
        assertFalse(firstChunkLastCharacter.isHighSurrogate())
        assertTrue(secondChunkFirstCharacter.isHighSurrogate())
    }

    @Test
    fun treatsBlankNoticeAsUnavailable() {
        assertTrue(LicenseNoticeChunker.chunk(" \n\t").isEmpty())
    }
}
