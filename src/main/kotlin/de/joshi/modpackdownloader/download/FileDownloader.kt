package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.auth.CurseforgeApiKey
import de.joshi.modpackdownloader.http.HttpService
import mu.KotlinLogging
import java.io.File
import java.io.IOException
import java.net.URL
import java.time.Instant

class FileDownloader {
    private val LOGGER = KotlinLogging.logger {  }
    private val httpService = HttpService(
        CurseforgeApiKey.getApiKey() ?: throw RuntimeException("No Value found for env CURSEFORGE_API_KEY")
    )

    fun downloadModFiles(targetDirectory: File, downloadUrls: List<URL>) {
        val startTime = Instant.now().toEpochMilli()

        targetDirectory.mkdirs()

        for((filesDownloaded, fileUrl) in downloadUrls.withIndex()) {
            try {
                httpService.downloadFile(fileUrl, targetDirectory)
                LOGGER.info { "$filesDownloaded out of ${downloadUrls.size} remaining" }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        LOGGER.info("Mod downloads completed, ${downloadUrls.size} files downloaded in ${Instant.now().toEpochMilli() - startTime}ms")
    }
}