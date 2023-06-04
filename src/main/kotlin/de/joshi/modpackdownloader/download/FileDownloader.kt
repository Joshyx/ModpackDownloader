package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.Main.Companion.LOGGER
import de.joshi.modpackdownloader.Main.Companion.fileNames
import de.joshi.modpackdownloader.http.HttpService
import de.joshi.modpackdownloader.models.FileData
import de.joshi.modpackdownloader.models.ModCategory
import de.joshi.modpackdownloader.models.ReadMeInfo
import de.joshi.modpackdownloader.util.getSubfolder
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.time.Instant
import kotlin.io.path.writeBytes
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class FileDownloader {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val downloadDispatcher = Dispatchers.IO.limitedParallelism(10)

    fun downloadModFiles(targetDirectory: File, downloadUrls: Map<Url, ModCategory>) {
        val startTime = Instant.now().toEpochMilli()
        val files = mutableListOf<FileData>()

        LOGGER.info { "Downloading files..." }

        runBlocking {
            downloadUrls.forEach { (fileUrl, category) ->
                withContext(downloadDispatcher) {
                    try {
                        HttpService.getFile(fileUrl, targetDirectory, category, downloadUrls.size)?.let {
                            files.add(it)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        ReadMeInfo.errors.add(e.stackTraceToString())
                    }
                }
            }
        }

        files.forEach { file ->
            if (!file.parentDirectory.exists()) file.parentDirectory.mkdirs()
            file.destination.writeBytes(file.responseBody)
            if (file.name !in fileNames) fileNames.add(file.name)
            LOGGER.info("Saved ${file.name} to ${file.destination}")
        }

        listOf("mods", "resourcepacks", "shaderpacks").forEach { subfolder ->
            targetDirectory.getSubfolder(subfolder)?.listFiles()?.forEach { file ->
                if (file.name !in fileNames && file.extension != "disabled") {
                    file.delete()
                    LOGGER.info("Removed $file from $subfolder")
                }
            }
        }

        LOGGER.info("File downloads completed, ${files.size} files downloaded in " +
                "${(Instant.now().toEpochMilli() - startTime).milliseconds.absoluteValue.coerceAtLeast(0.seconds)}")
    }

}