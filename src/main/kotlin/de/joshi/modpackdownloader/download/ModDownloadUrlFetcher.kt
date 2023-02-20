package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.models.ModData
import de.joshi.modpackdownloader.models.ModInfo
import de.joshi.modpackdownloader.models.ReadMeInfo
import de.joshi.modpackdownloader.util.getString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import mu.KotlinLogging
import java.lang.RuntimeException
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Objects

class ModDownloadUrlFetcher {
    private val LOGGER = KotlinLogging.logger {  }

    fun downloadUrlsForMods(manifest: ManifestData, requireAll: Boolean = true): List<URL> {

        return manifest.files.map {modData ->
            val modInfo = getModInfo(modData)
            if(requireAll || modData.required) {

                return@map modInfo.downloadURL ?: {
                        val error = """
                        MalformedUrlException: No download URL exists for ${modInfo.name}
                        - Try downloading it manually instead
                        - ${getManualDownloadUrl(modData.projectID, modData.fileID)}
                        - ${modInfo.downloadedInfoString}
                    """.trimIndent()
                        ReadMeInfo.errors.add(error)
                        LOGGER.error(error)
                }
            } else {
                LOGGER.info("Skipping download for ${modInfo.name}: Not required")
            }
        }.filterIsInstance(URL::class.java)
    }
    fun getModInfo(modData: ModData): ModInfo {
        val modInfo = downloadModInfo(modData.projectID, modData.fileID)["data"]?.jsonObject!!
        val name = modInfo["fileName"]!!.getString()
        var url: URL?
        try {
            url = URL(modInfo["downloadUrl"]!!.getString())
            LOGGER.info { "Parsed URL $url" }
        } catch (e: MalformedURLException) {

            try {
                url = URL(getAlternativeDownloadUrl(modInfo))
                LOGGER.info { "Parsed Fallback URL $url" }
            } catch (e: MalformedURLException) {
                url = null
            }
        }
        return ModInfo(name, url, modData.required, modInfo)
    }
    private fun downloadModInfo(projectId: Int, fileId: Int): JsonObject {
        return Json.decodeFromString(getHttpBody("https://api.curseforge.com/v1/mods/$projectId/files/$fileId"))
    }
    private fun getAlternativeDownloadUrl(modInfo: JsonObject): String {
        val id = modInfo["id"]!!.getString()
        val fileName = modInfo["fileName"]!!.getString()
        return "https://edge.forgecdn.net/files/${id.substring(0, 4)}/${id.substring(4)}/$fileName"
    }
    private fun getManualDownloadUrl(projectId: Int, fileId: Int): String {
        println(getHttpBody("https://api.curseforge.com/v1/mods/$projectId/"))
        val modInfo: Map<String, Any> = Json.decodeFromString(getHttpBody("https://api.curseforge.com/v1/mods/$projectId/"))
        return modInfo.toString()
    }
    private fun getHttpBody(url: String): String {
        val client = HttpClient.newBuilder().build()
        val apiKey = System.getenv("CURSEFORGE_API_KEY") ?: throw RuntimeException("No Value found for env CURSEFORGE_API_KEY")
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(url))
            .header("x-api-key", apiKey)
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
}