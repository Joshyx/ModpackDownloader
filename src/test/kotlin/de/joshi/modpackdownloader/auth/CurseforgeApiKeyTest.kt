package de.joshi.modpackdownloader.auth

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class CurseforgeApiKeyTest {
    @Test
    fun isApiKeyValidReturnsFalse() {
        assertFalse(CurseforgeApiKey.isApiKeyValid("hallo"))
    }
}