package de.joshi.modpackdownloader.models

import io.ktor.http.*
import kotlinx.serialization.json.JsonObject

data class ModInfo(
    val name: String,
    val downloadURL: Url?,
    val required: Boolean,
    val downloadedInfoString: JsonObject,
    val category: ModCategory
)
