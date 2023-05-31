package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.Main.Companion.LOGGER
import de.joshi.modpackdownloader.Main.Companion.fileNames
import de.joshi.modpackdownloader.Main.Companion.usedDirectories
import de.joshi.modpackdownloader.http.HttpService
import de.joshi.modpackdownloader.models.FileData
import de.joshi.modpackdownloader.models.ModCategory
import de.joshi.modpackdownloader.models.ReadMeInfo
import de.joshi.modpackdownloader.util.getSubfolder
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import java.net.URL
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.io.path.writeBytes

class FileDownloader {
    fun downloadModFiles(targetDirectory: File, downloadUrls: Map<URL, ModCategory>) {
        val startTime = Instant.now().toEpochMilli()
        val files = mutableListOf<FileData>()

        LOGGER.info { "Downloading files..." }

        runBlocking {
            downloadUrls.forEach { (fileUrl, category) ->
                launch {
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

        usedDirectories.forEach(File::mkdirs)

        files.forEach { file ->
            file.destination.writeBytes(file.responseBody)
            if (file.name !in fileNames) fileNames.add(file.name)
            LOGGER.info("Saved ${file.name} to ${file.destination}")
        }

        listOf("mods", "resourcepacks", "shaderpacks").forEach { subfolder ->
            targetDirectory.getSubfolder(subfolder)?.listFiles()?.forEach { file ->
                if (file.name !in fileNames) {
                    file.delete()
                    LOGGER.info("Removed $file from $subfolder")
                }
            }
        }

        LOGGER.info(
            "File downloads completed, ${files.size} files downloaded in ${
                Instant.now().toEpochMilli() - startTime
            }ms " +
                    "( ~${TimeUnit.MILLISECONDS.toMinutes(Instant.now().toEpochMilli() - startTime)}min )"
        )
    }

}