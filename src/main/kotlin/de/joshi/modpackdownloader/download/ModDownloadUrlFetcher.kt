package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.models.ModData
import de.joshi.modpackdownloader.models.ModInfo
import de.joshi.modpackdownloader.models.ReadMeInfo
import mu.KotlinLogging
import java.lang.RuntimeException
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ModDownloadUrlFetcher {
    private val LOGGER = KotlinLogging.logger {  }

    fun downloadUrlsForMods(manifest: ManifestData, requireAll: Boolean = true): List<URL> {

        return manifest.files.map {modData ->
            val modInfo = getModInfo(modData)
            try {

                if(requireAll || modData.required) {
                    LOGGER.info("Parsed URL ${modInfo.downloadURL}")
                    return@map URL(modInfo.downloadURL)
                } else {
                    LOGGER.info("Skipping download for ${modInfo.name}: Not required")
                }
            } catch(e: MalformedURLException) {
                val error = """
                    $e
                    - Error with download url for mod ${modInfo.name}
                    - Try downloading it manually instead.
                """.trimIndent()
                ReadMeInfo.errors.add(error)
                LOGGER.error(error)
            }
        }.filterIsInstance(URL::class.java)
    }
    fun getModInfo(modData: ModData): ModInfo {
        val modInfoString = downloadModInfo(modData.projectID, modData.fileID)
        val name = modInfoString.substringAfter("\"fileName\":\"").substringBefore("\"")
        val url = modInfoString.substringAfter("\"downloadUrl\":\"").substringBefore("\"")
        return ModInfo(name, url, modData.required, modInfoString)
    }
    private fun downloadModInfo(projectId: Int, fileId: Int): String {
        return getHttpBody("https://api.curseforge.com/v1/mods/$projectId/files/$fileId")
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