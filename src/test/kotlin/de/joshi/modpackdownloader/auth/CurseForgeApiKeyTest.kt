package de.joshi.modpackdownloader.auth

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CurseForgeApiKeyTest {
    @Test
    fun isApiKeyValidReturnsFalse() {
        assertFalse(CurseForgeApiKey.isApiKeyValid("hallo"))
    }

    @Test
    fun getApiKeyReturnsNotNull() {
        assertNotNull(CurseForgeApiKey.getApiKey())
    }

    @Test
    fun hasValidApiKeyReturnsTrue() {
        assert(CurseForgeApiKey.hasValidApiKey())
    }
}