package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.auth.CurseforgeApiKey
import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.models.ModData
import de.joshi.modpackdownloader.models.ModInfo
import de.joshi.modpackdownloader.models.ReadMeInfo
import de.joshi.modpackdownloader.util.getString
import kotlinx.serialization.SerializationException
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

class ModDownloadUrlFetcher {
    private val LOGGER = KotlinLogging.logger {  }

    fun downloadUrlsForMods(manifest: ManifestData, requireAll: Boolean = true): List<URL> {

        return manifest.files.map {modData ->
            try {
                val modInfo = getModInfo(modData)
                LOGGER.info( "Acquired file info for mod ${modInfo?.name} (Project Id: ${modData.projectID})" )
                if(requireAll || modData.required) {

                    return@map modInfo?.downloadURL ?: {
                        logError(
                            """
                                MalformedUrlException: No download URL exists for ${modInfo?.name}
                                - Try downloading it manually instead
                                - ${getManualDownloadUrl(modData.projectID, modData.fileID)}
                                - ${modInfo?.downloadedInfoString}
                            """
                        )
                    }
                } else {
                    LOGGER.info("Skipping download for ${modInfo?.name}: Not required")
                }
            } catch(e: SerializationException) {
                logError(
                    """
                            Error downloading mod with id ${modData.projectID}.
                            - Does this mod even exist?
                            - Try downloading it manually instead
                        """
                )
            }
        }.filterIsInstance(URL::class.java)
    }
    fun getModInfo(modData: ModData): ModInfo? {
        val modInfo = try {
            downloadFileInfo(modData.projectID, modData.fileID)["data"]?.jsonObject!!
        } catch (e: Exception) {
            LOGGER.error("Error with downloading mod info for mod ${modData.projectID}: ${getHttpBody("https://api.curseforge.com/v1/mods/${modData.projectID}/files/${modData.fileID}")}")
            return null
        }

        val name = modInfo["fileName"]!!.getString()
        var url: URL?
        try {
            url = URL(modInfo["downloadUrl"]!!.getString())
            LOGGER.info { "Parsed URL $url" }
        } catch (e: Exception) {

            try {
                url = URL(getAlternativeDownloadUrl(modInfo))
                LOGGER.info { "Parsed Fallback URL $url" }
            } catch (e: Exception) {
                LOGGER.error { "No url found for mod ${modData.projectID}" }
                url = null
            }
        }
        return ModInfo(name, url, modData.required, modInfo)
    }
    fun downloadFileInfo(projectId: Int, fileId: Int): JsonObject {
        return Json.decodeFromString(getHttpBody("https://api.curseforge.com/v1/mods/$projectId/files/$fileId"))
    }
    fun getAlternativeDownloadUrl(modInfo: JsonObject): String {
        val id = modInfo["id"]!!.getString()
        val fileName = modInfo["fileName"]!!.getString()
        return "https://edge.forgecdn.net/files/${id.substring(0, 4)}/${id.substring(4)}/$fileName"
    }
    fun getManualDownloadUrl(projectId: Int, fileId: Int): String {
        val modInfo: JsonObject = Json.decodeFromString(getHttpBody("https://api.curseforge.com/v1/mods/$projectId/"))
        val modSlugName = modInfo["data"]?.jsonObject?.get("slug")?.getString()
        return "https://www.curseforge.com/minecraft/mc-mods/$modSlugName/download/$fileId"
    }
    fun getHttpBody(url: String): String {
        val client = HttpClient.newBuilder().build()
        val apiKey = CurseforgeApiKey.getApiKey() ?: throw RuntimeException("No Value found for env CURSEFORGE_API_KEY")
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(url))
            .header("x-api-key", apiKey)
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
    private fun logError(error: String) {

        ReadMeInfo.errors.add(error)
        LOGGER.error(error)
    }
}