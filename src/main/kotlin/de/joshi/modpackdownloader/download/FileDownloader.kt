package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.Main.Companion.LOGGER
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

    fun downloadModFiles(targetDirectory: File, downloadUrls: List<URL>) {
        val startTime = Instant.now().toEpochMilli()
        val files = mutableListOf<FileData>()
        val fileNames = mutableListOf<String>()

        LOGGER.info { "Downloading files..." }

        runBlocking {
            for (fileUrl in downloadUrls) {
                launch {
                    try {
                        HttpService.getFile(fileUrl, targetDirectory, downloadUrls.size)?.let {
                            files.add(it)
                        } ?: run {
                            fileNames.add(fileUrl.file.substringAfterLast("/"))
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        ReadMeInfo.errors.add(e.stackTraceToString())
                    }
                }
            }
        }

        targetDirectory.mkdirs()

        // Save files
        files.forEach { (fileName, responseBody, destinationFile) ->
            destinationFile.writeBytes(responseBody)
            fileNames.add(fileName)
            LOGGER.info("Saved $fileName to $destinationFile")
        }

        // Remove other files
        targetDirectory.listFiles()?.forEach { file ->
            if (file.name !in fileNames) {
                file.delete()
            }
        }

        LOGGER.info(
            "Mod downloads completed, ${files.size} files downloaded in ${
                Instant.now().toEpochMilli() - startTime
            }ms " +
                    "( ~${TimeUnit.MILLISECONDS.toMinutes(Instant.now().toEpochMilli() - startTime)}min )"
        )
    }

}