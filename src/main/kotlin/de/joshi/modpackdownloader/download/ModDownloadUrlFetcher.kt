package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.models.ManifestData
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

    fun downloadUrlsForMods(manifest: ManifestData): List<URL> {

        return manifest.files.map {modInfo ->
            val modInfoString = getModInfo(modInfo.projectID, modInfo.fileID)
            try {
                getModDownloadUrl(modInfoString)
            } catch(e: MalformedURLException) {
                LOGGER.error(
                    """
                        ${e}
                        - Error with download url for mod ${modInfoString.substringAfter("\"displayName\":\"").substringBefore("\",")}
                        - Try downloading it manually instead.
                    """.trimIndent()
                )
            }
        }.filterIsInstance(URL::class.java)
    }
    fun getModInfo(projectId: Int, fileId: Int): String {
        val client = HttpClient.newBuilder().build()
        val apiKey = System.getenv("CURSEFORGE_API_KEY") ?: throw RuntimeException("No Value found for env CURSEFORGE_API_KEY")
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.curseforge.com/v1/mods/$projectId/files/$fileId"))
            .header("x-api-key", apiKey)
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
    fun getModDownloadUrl(modInfo: String): URL {
        val url = modInfo.substringAfter("\"downloadUrl\":\"").substringBefore("\"")
        LOGGER.info("Parsed URL $url")
        return URL(url)
    }
}