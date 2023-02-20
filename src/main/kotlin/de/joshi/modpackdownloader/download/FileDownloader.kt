package de.joshi.modpackdownloader.download

import mu.KotlinLogging
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.Instant
import kotlin.io.path.exists

class FileDownloader {
    private val LOGGER = KotlinLogging.logger {  }

    fun downloadFiles(targetDirectory: File, downloadUrls: List<URL>, emptyDirectory: Boolean = false) {
        var filesDownloaded = 0
        val startTime = Instant.now().toEpochMilli()

        prepareTargetDirectory(targetDirectory, emptyDirectory)

        for(fileUrl in downloadUrls) {
            filesDownloaded++
            downloadFile(fileUrl, targetDirectory, filesDownloaded, downloadUrls)
        }
        LOGGER.info("Mod downloads completed, ${downloadUrls.size} files downloaded in ${Instant.now().toEpochMilli() - startTime}ms")
    }

    private fun downloadFile(
        fileUrl: URL,
        targetDirectory: File,
        filesDownloaded: Int,
        downloadUrls: List<URL>
    ) {
        val fileName = fileUrl.file.substringAfterLast("/")
        val destinationFile = Paths.get("$targetDirectory/$fileName")
        LOGGER.info("Downloading file $fileName...")
        if (!destinationFile.exists()) {
            fileUrl.openStream().use {
                Files.copy(it, destinationFile, StandardCopyOption.REPLACE_EXISTING)
            }
            LOGGER.info("Saved $fileName to $destinationFile (File $filesDownloaded of ${downloadUrls.size}, ${downloadUrls.size - filesDownloaded} remaining)")
        } else {
            LOGGER.info("File $fileName already exists. (File $filesDownloaded of ${downloadUrls.size}, ${downloadUrls.size - filesDownloaded} remaining)")
        }
    }

    private fun prepareTargetDirectory(targetDirectory: File, emptyDirectory: Boolean) {
        if (!targetDirectory.exists()) {
            LOGGER.info("Creating directory $targetDirectory to store files in")
            targetDirectory.mkdirs()
            return
        }

        if (!targetDirectory.listFiles().isNullOrEmpty()) {
            return
        }

        if (emptyDirectory) {
            LOGGER.warn("Deleting files in directory $targetDirectory. This significantly impacts performance so disable it to make it much faster")
            targetDirectory.deleteRecursively()
            targetDirectory.mkdirs()
        } else {
            LOGGER.warn("The directory $targetDirectory is not empty, Continuing to save files there. Enable emptyDirectory to automatically delete files")
        }
    }
}