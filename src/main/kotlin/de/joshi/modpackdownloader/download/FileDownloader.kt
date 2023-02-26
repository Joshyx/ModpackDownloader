package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.auth.CurseforgeApiKey
import de.joshi.modpackdownloader.http.HttpService
import mu.KotlinLogging
import java.io.File
import java.lang.RuntimeException
import java.net.URL
import java.time.Instant

class FileDownloader {
    private val LOGGER = KotlinLogging.logger {  }
    private val httpService = HttpService(
        CurseforgeApiKey.getApiKey() ?: throw RuntimeException("No Value found for env CURSEFORGE_API_KEY")
    )

    fun downloadModFiles(targetDirectory: File, downloadUrls: List<URL>) {
        var filesDownloaded = 0
        val startTime = Instant.now().toEpochMilli()

        targetDirectory.mkdirs()

        for(fileUrl in downloadUrls) {
            filesDownloaded++
            httpService.downloadFile(fileUrl, targetDirectory)
            LOGGER.info { "$filesDownloaded out of ${downloadUrls.size} remaining" }
        }
        LOGGER.info("Mod downloads completed, ${downloadUrls.size} files downloaded in ${Instant.now().toEpochMilli() - startTime}ms")
    }
}