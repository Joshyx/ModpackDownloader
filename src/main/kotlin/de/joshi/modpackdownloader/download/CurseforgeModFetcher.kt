package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.auth.CurseforgeApiKey
import de.joshi.modpackdownloader.http.HttpService
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
import java.net.URL

class CurseforgeModFetcher {
    private val LOGGER = KotlinLogging.logger {  }
    private val httpService = HttpService(
        CurseforgeApiKey.getApiKey() ?: throw RuntimeException("No Value found for env CURSEFORGE_API_KEY")
    )

    fun fetchUrlsForMods(manifest: ManifestData, requireAll: Boolean = true): List<URL> {

        return manifest.files.map {modData ->
            try {
                val modInfo = fetchModInfo(modData)
                LOGGER.info( "Acquired file info for mod ${modInfo?.name} (Project Id: ${modData.projectID})" )
                if(requireAll || modData.required) {

                    return@map modInfo?.downloadURL ?: {
                        logError(
                            """
                                MalformedUrlException: No download URL exists for ${modInfo?.name}
                                - Try downloading it manually instead
                                - ${fetchManualDownloadUrl(modData.projectID, modData.fileID)}
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
    fun fetchModInfo(modData: ModData): ModInfo? {
        val modInfo = try {
            fetchFileInfo(modData.projectID, modData.fileID)["data"]?.jsonObject!!
        } catch (e: Exception) {
            LOGGER.error("Error with downloading mod info for mod ${modData.projectID}: ${httpService.getHttpBody("https://api.curseforge.com/v1/mods/${modData.projectID}/files/${modData.fileID}")}")
            return null
        }

        val name = modInfo["fileName"]!!.getString()
        var url: URL?
        try {
            url = URL(modInfo["downloadUrl"]!!.getString())
            LOGGER.info { "Parsed URL $url" }
        } catch (e: Exception) {

            try {
                url = URL(fetchAlternativeDownloadUrl(modInfo))
                LOGGER.info { "Parsed Fallback URL $url" }
            } catch (e: Exception) {
                LOGGER.error { "No url found for mod ${modData.projectID}" }
                url = null
            }
        }
        return ModInfo(name, url, modData.required, modInfo)
    }
    fun fetchFileInfo(projectId: Int, fileId: Int): JsonObject {
        return Json.decodeFromString(httpService.getHttpBody("https://api.curseforge.com/v1/mods/$projectId/files/$fileId"))
    }
    fun fetchAlternativeDownloadUrl(modInfo: JsonObject): String {
        val id = modInfo["id"]!!.getString()
        val fileName = modInfo["fileName"]!!.getString()
        return "https://edge.forgecdn.net/files/${id.substring(0, 4)}/${id.substring(4)}/$fileName"
    }
    fun fetchManualDownloadUrl(projectId: Int, fileId: Int): String {
        val modInfo: JsonObject = Json.decodeFromString(httpService.getHttpBody("https://api.curseforge.com/v1/mods/$projectId/"))
        val modSlugName = modInfo["data"]?.jsonObject?.get("slug")?.getString()
        return "https://www.curseforge.com/minecraft/mc-mods/$modSlugName/download/$fileId"
    }

    private fun logError(error: String) {

        ReadMeInfo.errors.add(error)
        LOGGER.error(error)
    }
}