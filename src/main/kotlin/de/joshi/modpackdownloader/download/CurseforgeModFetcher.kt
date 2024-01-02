package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.Main.Companion.LOGGER
import de.joshi.modpackdownloader.Main.Companion.baseURL
import de.joshi.modpackdownloader.Main.Companion.fileNames
import de.joshi.modpackdownloader.http.HttpService
import de.joshi.modpackdownloader.models.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CurseforgeModFetcher {
    val json = Json {
        ignoreUnknownKeys = true
    }

    fun fetchUrlsForMods(manifest: ManifestData, requireAll: Boolean = true): Map<Url, ModCategory> {
        return runBlocking {
            val result = mutableMapOf<Url, ModCategory>()
            manifest.files.forEach { modData ->
                launch {
                    try {
                        val modInfo = fetchModInfo(modData)

                        LOGGER.info(
                            "Acquired file info for ${modInfo?.category?.getName()} " +
                                    "${modInfo?.name} (Project Id: ${modData.projectID})"
                        )

                        modInfo?.name?.let {
                            if (it !in fileNames) fileNames.add(it)
                        }

                        if (requireAll || modData.required) {
                            if (modInfo?.downloadURL != null) {
                                result[modInfo.downloadURL] = modInfo.category
                            } else {
                                logError(
                                    """
                                        MalformedUrlException: No download URL exists for ${modInfo?.name}
                                        - Try downloading it manually instead
                                        - ${fetchManualDownloadUrl(modData.projectID, modData.fileID)}
                                        - ${modInfo?.toString()}
                                        """
                                )
                            }
                        } else LOGGER.info("Skipping download for ${modInfo?.name}: Not required")
                    } catch (e: SerializationException) {
                        logError(
                            """
                                Error downloading file with id ${modData.projectID}.
                                - Does this file even exist?
                                - Try downloading it manually instead
                                """
                        )
                    }
                }
            }
            return@runBlocking result
        }
    }

    suspend fun fetchModInfo(modData: ModData): ModInfo? {
        val category = fetchCategory(modData.projectID)
        val modInfo: RawCurseForgeFileInfo? = withContext(Dispatchers.Default) {
            try {
                fetchFileInfo(modData.projectID, modData.fileID)
            } catch (e: Exception) {
                LOGGER.error(
                    "Error with downloading file info for ${category.getName()} ${modData.projectID}: ${
                        HttpService.getHttpBody(
                            "$baseURL/v1/mods/${modData.projectID}/files/${modData.fileID}"
                        )
                    }"
                )
                logError("Error with downloading file info for ${category.getName()} with ID: ${modData.projectID}")
                return@withContext null
            }
        }

        if (modInfo == null) return null

        val name = modInfo.fileName.replace("%2b", "+")
        val url = fetchUrl(modInfo, category, modData)

        return ModInfo(name, url, modData.required, modInfo, category)
    }

    suspend fun fetchFileInfo(projectId: Int, fileId: Int): RawCurseForgeFileInfo {
        return json.decodeFromString<RawCurseForgeFileInfoWrapper>(HttpService.getHttpBody("$baseURL/v1/mods/$projectId/files/$fileId")).data
    }

    private suspend fun fetchCategory(projectId: Int): ModCategory = withContext(Dispatchers.Default) {
        val response = try {
            json.decodeFromString<RawCurseForgeModInfoWrapper>(HttpService.getHttpBody("$baseURL/v1/mods/$projectId")).data
        } catch (e: Exception) {
            LOGGER.error("Error with fetching file category for file with ID: $projectId")
            logError("Error with fetching file category for file with ID: $projectId")

            return@withContext ModCategory.MOD // Fallback to mods folder
        }
        val category = response.classId

        return@withContext when (category) {
            6 -> ModCategory.MOD
            12 -> ModCategory.RESOURCE_PACK
            4546 -> ModCategory.SHADER_PACK
            4471 -> ModCategory.MOD_PACK

            else -> ModCategory.MOD // Fallback to mods folder
        }
    }

    fun fetchUrl(modInfo: RawCurseForgeFileInfo, category: ModCategory, modData: ModData): Url? {
        var url: String?
        try {
            url = modInfo.downloadUrl
            if (url == "http://localhost/null") {
                url = fetchAlternativeDownloadUrl(modInfo)
                LOGGER.info { "Parsed Fallback URL $url" }
            } else {
                LOGGER.info { "Parsed URL $url" }
            }
        } catch (e: Exception) {
            logError("No url found for ${category.getName()} ${modData.projectID}")
            url = null
        }
        return url?.let { Url(it.replace("%2b", "+")) }
    }

    fun fetchAlternativeDownloadUrl(modInfo: RawCurseForgeFileInfo): String {
        val id = modInfo.id.toString()
        val fileName = modInfo.fileName
        return "https://edge.forgecdn.net/files/${id.substring(0, 4)}/${id.substring(4)}/$fileName"
    }

    suspend fun fetchManualDownloadUrl(projectId: Int, fileId: Int): String {
        val modInfo = json.decodeFromString<RawCurseForgeModInfoWrapper>(HttpService.getHttpBody("$baseURL/v1/mods/$projectId")).data
        val modSlugName = modInfo.slug
        return "https://www.curseforge.com/minecraft/mc-mods/$modSlugName/download/$fileId"
    }

    private fun logError(error: String) {
        ReadMeInfo.errors.add(error)
        LOGGER.error(error)
    }

}