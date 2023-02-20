package de.joshi.modpackdownloader.util

import kotlinx.serialization.json.JsonElement

fun JsonElement.getString(): String {
    return toString().replace("\"", "")
}