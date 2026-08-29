package com.nagopy.android.aplin.ui.licenses

import org.junit.Assert.assertEquals
import org.junit.Test

class LicenseCatalogParserTest {
    @Test
    fun parseDisplaysCoordinateAndEveryLicenseField() {
        val json =
            """
            [{
              "groupId":"com.example",
              "artifactId":"fallback-artifact",
              "version":"1.2.3",
              "name":"Readable Name",
              "spdxLicenses":[
                {"identifier":"Apache-2.0","name":"Apache License 2.0","url":"https://spdx.org/licenses/Apache-2.0.html"},
                {"identifier":"MIT","name":"MIT License","url":"https://spdx.org/licenses/MIT.html"}
              ],
              "unknownLicenses":[{"name":"Custom License","url":"https://example.com/license"}],
              "scm":{"url":"https://example.com/source"}
            }]
            """.trimIndent()

        assertEquals(
            LicenseEntry(
                groupId = "com.example",
                artifactId = "fallback-artifact",
                version = "1.2.3",
                name = "Readable Name",
                licenses =
                    listOf(
                        LicenseReference(
                            name = "Apache License 2.0",
                            identifier = "Apache-2.0",
                            url = "https://spdx.org/licenses/Apache-2.0.html",
                        ),
                        LicenseReference(
                            name = "MIT License",
                            identifier = "MIT",
                            url = "https://spdx.org/licenses/MIT.html",
                        ),
                        LicenseReference(
                            name = "Custom License",
                            identifier = "",
                            url = "https://example.com/license",
                        ),
                    ),
                sourceUrl = "https://example.com/source",
            ),
            LicenseCatalogParser().parse(json).single(),
        )
    }
}
