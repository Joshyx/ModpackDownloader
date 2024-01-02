package de.joshi.modpackdownloader

import de.joshi.modpackdownloader.auth.CurseforgeApiKey
import de.joshi.modpackdownloader.download.CurseforgeModFetcher
import de.joshi.modpackdownloader.download.FileDownloader
import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.models.ModCategory
import de.joshi.modpackdownloader.overrides.OverridesHandler
import de.joshi.modpackdownloader.parser.ManifestParser
import de.joshi.modpackdownloader.readme.ModlistService
import de.joshi.modpackdownloader.readme.ReadMeMarkdownService
import de.joshi.modpackdownloader.zip.UnzipService
import io.github.oshai.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import java.io.File
import java.time.Instant
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
    val sourceFile = File(args.getOrNull(0).also {
        if (it.isNullOrEmpty()) throw IllegalArgumentException("The source directory cannot be null or empty")
    }!!)
    val targetDirectory = File(args.getOrNull(1) ?: sourceFile.nameWithoutExtension)

    Main().run(sourceFile, targetDirectory)
}

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
        var baseURL = "https://cfproxy.bmpm.workers.dev"
        val fileNames = ArrayList<String>()
    }

    fun run(sourceFile: File, targetDirectory: File) {
        val startTime = Instant.now().toEpochMilli()
        LOGGER.info { "Running Modpack Downloader from $sourceFile to $targetDirectory" }

        if (CurseforgeApiKey.hasValidApiKey()) {
            baseURL = "https://api.curseforge.com"
            LOGGER.info { "API Key detected" }
        } else {
            LOGGER.info { "API Key not detected, using cfproxy" }
        }

        val sourceDirectory = if (sourceFile.extension == "zip") {
            UnzipService().unzip(sourceFile, File(targetDirectory, "originalModPack"))
        } else sourceFile

        // Parse Manifest
        val manifest: ManifestData = ManifestParser().getManifest(sourceDirectory)

        val modListDownloadStartTime = Instant.now().toEpochMilli()

        // Fetch URLs
        val modList: Map<Url, ModCategory> = CurseforgeModFetcher().fetchUrlsForMods(manifest, false)

        LOGGER.info(
            "Fetched ${modList.size} mod URLs in " +
                    "${(Instant.now().toEpochMilli() - modListDownloadStartTime).milliseconds.absoluteValue.coerceAtLeast(0.seconds)}"
        )

        // Download mods
        FileDownloader().downloadModFiles(targetDirectory, modList)

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
        LOGGER.info("Process finished in " +
                "${(Instant.now().toEpochMilli() - startTime).milliseconds.absoluteValue.coerceAtLeast(0.seconds)}")

        exitProcess(0)
    }
}