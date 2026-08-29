package com.nagopy.android.aplin.ui.licenses

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class LicenseCatalogParser {
    fun parse(json: String): List<LicenseEntry> {
        val artifacts = jsonParser.parseToJsonElement(json).jsonArray
        return artifacts.map { it.jsonObject.toLicenseEntry() }
    }

    private fun JsonObject.toLicenseEntry(): LicenseEntry =
        LicenseEntry(
            groupId = getString("groupId"),
            artifactId = getString("artifactId"),
            version = getString("version"),
            name = getString("name"),
            licenses =
                listOf("spdxLicenses", "unknownLicenses")
                    .flatMap { getLicenseReferences(it) },
            sourceUrl =
                get("scm")
                    ?.jsonObject
                    ?.getString("url")
                    .orEmpty(),
        )

    private fun JsonObject.getLicenseReferences(key: String): List<LicenseReference> =
        get(key)
            ?.jsonArray
            ?.map { license ->
                license.jsonObject.let { licenseObject ->
                    LicenseReference(
                        name = licenseObject.getString("name"),
                        identifier = licenseObject.getString("identifier"),
                        url = licenseObject.getString("url"),
                    )
                }
            }.orEmpty()

    private fun JsonObject.getString(key: String): String =
        get(key)
            ?.jsonPrimitive
            ?.contentOrNull
            .orEmpty()

    private companion object {
        val jsonParser = Json { ignoreUnknownKeys = true }
    }
}
