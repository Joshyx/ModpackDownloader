package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.Main.Companion.LOGGER
import de.joshi.modpackdownloader.auth.CurseforgeApiKey
import de.joshi.modpackdownloader.http.HttpService
import de.joshi.modpackdownloader.models.FileData
import de.joshi.modpackdownloader.models.ReadMeInfo
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import java.net.URL
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.io.path.writeBytes

class FileDownloader {

    private val httpService = HttpService(
        CurseforgeApiKey.getApiKey() ?: throw RuntimeException("No Value found for env CURSEFORGE_API_KEY")
    )

    fun downloadModFiles(targetDirectory: File, downloadUrls: List<URL>) {
        val startTime = Instant.now().toEpochMilli()
        val files = mutableListOf<FileData>()

        runBlocking {
            for (fileUrl in downloadUrls) {
                launch {
                    try {
                        files.add(httpService.getFile(fileUrl, targetDirectory, downloadUrls.size))
                    } catch (e: IOException) {
                        e.printStackTrace()
                        ReadMeInfo.errors.add(e.stackTraceToString())
                    }
                }
            }
        }

        targetDirectory.mkdirs()

        files.forEach { (fileName, responseBody, destinationFile) ->
            destinationFile.writeBytes(responseBody)
            LOGGER.info("Saved $fileName to $destinationFile")
        }

        LOGGER.info(
            "Mod downloads completed, ${files.size} files downloaded in ${Instant.now().toEpochMilli() - startTime}ms " +
                    "( ~${ TimeUnit.MILLISECONDS.toMinutes(Instant.now().toEpochMilli() - startTime)}min )"
        )
    }

}