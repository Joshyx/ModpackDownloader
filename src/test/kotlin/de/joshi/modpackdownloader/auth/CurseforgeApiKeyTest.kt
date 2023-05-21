package de.joshi.modpackdownloader.auth

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CurseforgeApiKeyTest {
    @Test
    fun isApiKeyValidReturnsFalse() {
        assertFalse(CurseforgeApiKey.isApiKeyValid("hallo"))
    }

    @Test
    fun getApiKeyReturnsNotNull() {
        assertNotNull(CurseforgeApiKey.getApiKey())
    }

    @Test
    fun hasValidApiKeyReturnsTrue() {
        assert(CurseforgeApiKey.hasValidApiKey())
    }
}