package de.joshi.modpackdownloader.util

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonUtilsTest {

    @Test
    fun getString() {
        assertEquals("moin", Json.decodeFromString<JsonObject>("{\"name\":\"moin\"}")["name"]?.getString())
    }
}