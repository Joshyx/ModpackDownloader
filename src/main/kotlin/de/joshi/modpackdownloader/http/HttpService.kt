package de.joshi.modpackdownloader.http

import de.joshi.modpackdownloader.Main.Companion.LOGGER
import de.joshi.modpackdownloader.Main.Companion.client
import de.joshi.modpackdownloader.auth.CurseforgeApiKey
import de.joshi.modpackdownloader.models.FileData
import de.joshi.modpackdownloader.models.ModCategory
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

object HttpService {

    private val filesDownloaded = mutableMapOf<String, String>()
    private var skippedCount = 0
    private var previousProgress = 0

    fun getHttpBody(url: String, vararg apiKey: String?): String {
        return runBlocking {
            val httpResponse: HttpResponse = client.get(url) {
                headers {
                    apiKey.getOrNull(0)?.let {
                        append("x-api-key", it)
                    } ?: run {
                        CurseforgeApiKey.getApiKey()?.let { append("x-api-key", it) }
                    }
                }
            }
            return@runBlocking httpResponse.body<String>()
        }
    }

    /**
     * Starts downloading a file if the file does not already exist on the path.
     */
    suspend fun getFile(fileUrl: Url, targetDirectory: File, category: ModCategory, fileCount: Int): FileData? = withContext(Dispatchers.IO) {
        val fileName: String = fileUrl.toString().substringAfterLast("/")
        val parentDirectory = File(targetDirectory, category.getFolderName())
        val destination: Path = Paths.get("$parentDirectory/$fileName")

        if (destination.exists()) {
            LOGGER.info("Skipping $fileName, already exist")
            skippedCount += 1
            return@withContext null
        }

        val url: String = if (fileUrl.toString().contains(" ")) {
            fileUrl.toString().replace(" ", "%20")
        } else fileUrl.toString()

        val httpResponse: HttpResponse = client.get(url) {

            onDownload { min, max ->
                val progress = filesDownloaded.size * 100 / (fileCount - skippedCount)

                if ((min * 100 / max) > 90 && url !in filesDownloaded) {
                    LOGGER.info("Downloading $fileName")
                    filesDownloaded[url] = fileName
                }

                if (progress != previousProgress && progress == 100) LOGGER.info("Done!")

                previousProgress = progress
            }
        }
        val responseBody: ByteArray = httpResponse.body()

        return@withContext FileData(fileName, responseBody, destination, parentDirectory)
    }
}