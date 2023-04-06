package de.joshi.modpackdownloader

import de.joshi.modpackdownloader.auth.CurseforgeApiKey
import de.joshi.modpackdownloader.download.CurseforgeModFetcher
import de.joshi.modpackdownloader.download.FileDownloader
import de.joshi.modpackdownloader.overrides.OverridesHandler
import de.joshi.modpackdownloader.parser.ManifestParser
import de.joshi.modpackdownloader.readme.ModlistService
import de.joshi.modpackdownloader.readme.ReadMeMarkdownService
import de.joshi.modpackdownloader.zip.UnzipService
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import mu.KotlinLogging
import java.io.File
import java.time.Instant
import java.util.concurrent.TimeUnit

class Main {

    companion object {
        val LOGGER = KotlinLogging.logger { }
        val client = HttpClient(OkHttp) {
            install(HttpTimeout) {
                socketTimeoutMillis = 600000
            }
            engine {
                threadsCount = 30
                pipelining = true
                config {
                    retryOnConnectionFailure(true)
                }
            }
        }
    }

    fun run(sourceFile: File, targetDirectory: File, emptyDirectory: Boolean = false) {
        val startTime = Instant.now().toEpochMilli()
        LOGGER.info { "Running ModDownloader from $sourceFile to $targetDirectory" }
        LOGGER.info { "API Key detected" }

        if (emptyDirectory) {
            targetDirectory.deleteRecursively()
            targetDirectory.mkdirs()
            LOGGER.info { "Deleted all files in $targetDirectory" }
        }

        val sourceDirectory = if (sourceFile.extension == "zip") {
            UnzipService().unzip(sourceFile, File(targetDirectory, "originalModPack"))
        } else sourceFile

        val manifest = ManifestParser().getManifest(sourceDirectory)

        val modListDownloadStartTime = Instant.now().toEpochMilli()

        // Fetch URLs
        val modList = CurseforgeModFetcher().fetchUrlsForMods(manifest, false)

        LOGGER.info(
            "Fetched ${modList.size} mod URLs in ${Instant.now().toEpochMilli() - modListDownloadStartTime}ms " +
                    "( ${TimeUnit.MILLISECONDS.toSeconds(Instant.now().toEpochMilli() - modListDownloadStartTime)}sec )"
        )

        // Download mods
        FileDownloader().downloadModFiles(File(targetDirectory, "mods"), modList)

        // Handle overrides
        val overridesHandler = OverridesHandler()
        val overridesFolder = overridesHandler.getOverridesFolder(sourceDirectory, manifest)
        if (overridesFolder.exists()) {
            overridesHandler.handleOverrides(overridesFolder, targetDirectory)
        }

        ModlistService().createModlist(sourceDirectory, targetDirectory, overridesFolder)
        ReadMeMarkdownService().saveReadMe(targetDirectory, manifest)

        LOGGER.info("COMPLETED")
        LOGGER.info("Saved all files to $targetDirectory")
        LOGGER.info("Process finished in ${Instant.now().toEpochMilli() - startTime}ms " +
                "( ~${TimeUnit.MILLISECONDS.toMinutes(Instant.now().toEpochMilli() - startTime)}min )"
        )
    }
}

fun main(args: Array<String>) {

    val sourceFile = File(args.getOrNull(0).also {
        if (it.isNullOrEmpty()) throw IllegalArgumentException("The source directory cannot be null or empty")
    }!!)
    val targetDirectory = File(args.getOrNull(1) ?: sourceFile.nameWithoutExtension)

    CurseforgeApiKey.getApiKey() ?: throw RuntimeException("No Value found for env CURSEFORGE_API_KEY")

    // Logback
    File("$targetDirectory/info.log").delete()
    System.setProperty("moddownloader.logFile.path", "$targetDirectory/info.log")

    Main().run(sourceFile, targetDirectory, false)
}