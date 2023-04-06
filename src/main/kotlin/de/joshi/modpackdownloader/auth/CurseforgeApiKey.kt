package de.joshi.modpackdownloader.auth

import de.joshi.modpackdownloader.http.HttpService

object CurseforgeApiKey {

    fun getApiKey(): String? {
        return if (hasValidApiKey()) System.getenv("CURSEFORGE_API_KEY") ?: null else null
    }

    fun hasValidApiKey(): Boolean {
        return isApiKeyValid(System.getenv("CURSEFORGE_API_KEY") ?: return false)
    }

    fun isApiKeyValid(apiKey: String): Boolean {
        return HttpService(apiKey).getHttpBody("https://api.curseforge.com/v1/games").isNotBlank()
    }
}