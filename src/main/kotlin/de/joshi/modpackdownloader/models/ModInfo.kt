package de.joshi.modpackdownloader.models

import kotlinx.serialization.json.JsonObject
import java.net.URL

data class ModInfo (
    val name: String,
    val downloadURL: URL?,
    val required: Boolean,
    val downloadedInfoString: JsonObject
)
