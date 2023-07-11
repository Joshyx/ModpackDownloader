package de.joshi.modpackdownloader.auth

import de.joshi.modpackdownloader.http.HttpService
import kotlinx.coroutines.runBlocking

object CurseforgeApiKey {

    fun getApiKey(): String? {
        return if (hasValidApiKey()) System.getenv("CURSEFORGE_API_KEY") ?: null else null
    }

    fun hasValidApiKey(): Boolean {
        return isApiKeyValid(System.getenv("CURSEFORGE_API_KEY") ?: return false)
    }

    fun isApiKeyValid(apiKey: String): Boolean = runBlocking {
        return@runBlocking HttpService.getHttpBody("https://api.curseforge.com/v1/games", apiKey).isNotBlank()
    }
}