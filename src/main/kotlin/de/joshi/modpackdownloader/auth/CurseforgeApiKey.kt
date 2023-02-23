package de.joshi.modpackdownloader.auth

import java.lang.RuntimeException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object CurseforgeApiKey {

    fun getApiKey(): String? {
        return if(hasValidApiKey()) System.getenv("CURSEFORGE_API_KEY") ?: null else null
    }

    fun hasValidApiKey(): Boolean {
        return isApiKeyValid(System.getenv("CURSEFORGE_API_KEY") ?: return false)
    }

    fun isApiKeyValid(apiKey: String): Boolean {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.curseforge.com/v1/games"))
            .header("x-api-key", apiKey)
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body().isNotBlank()
    }
}