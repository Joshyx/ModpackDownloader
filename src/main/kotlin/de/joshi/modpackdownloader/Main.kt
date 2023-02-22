package de.joshi.modpackdownloader

import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import de.joshi.modpackdownloader.auth.CurseforgeApiKey
import de.joshi.modpackdownloader.download.FileDownloader
import de.joshi.modpackdownloader.download.ModDownloadUrlFetcher
import de.joshi.modpackdownloader.models.ReadMeInfo
import de.joshi.modpackdownloader.overrides.OverridesHandler
import de.joshi.modpackdownloader.parser.ManifestParser
import de.joshi.modpackdownloader.readme.ModlistService
import de.joshi.modpackdownloader.readme.ReadMeMarkdownService
import de.joshi.modpackdownloader.util.getOrCreateSubfolder
import de.joshi.modpackdownloader.util.getSubfolder
import de.joshi.modpackdownloader.zip.UnzipService
import mu.KotlinLogging
import mu.toKLogger
import java.io.File
import java.time.Instant
import java.util.logging.LogManager

class Main {
    private val LOGGER = KotlinLogging.logger {  }

    fun run(sourceFile: File, targetDirectory: File, emptyDirectory: Boolean = false) {
        val startTime = Instant.now().toEpochMilli()
        LOGGER.info { "Running ModDownloader from $sourceFile to $targetDirectory" }
        LOGGER.info { "API Key ${CurseforgeApiKey.getApiKey()} detected" }

        if(emptyDirectory) {
            targetDirectory.deleteRecursively()
            targetDirectory.mkdirs()
            LOGGER.info { "Deleted all files in $targetDirectory" }
        }

        val sourceDirectory = UnzipService().unzip(sourceFile, File(targetDirectory, "originalModPack"))

        val manifest = ManifestParser().getManifest(sourceDirectory)

        val modListDownloadStartTime = Instant.now().toEpochMilli()
        val modList = ModDownloadUrlFetcher().downloadUrlsForMods(manifest, false)
        LOGGER.info("Downloaded ${modList.size} mod URLs in ${Instant.now().toEpochMilli() - modListDownloadStartTime}ms")

        FileDownloader().downloadFiles(
            File(targetDirectory, "mods"),
            modList
        )

        val overridesHandler = OverridesHandler()
        val overridesFolder = overridesHandler.getOverridesFolder(sourceFile, manifest)
        if (overridesFolder != null) {
            overridesHandler.handleOverrides(overridesFolder, targetDirectory)
        }

        ModlistService().createModlist(sourceDirectory, targetDirectory, overridesFolder)
        ReadMeMarkdownService().saveReadMe(targetDirectory, manifest)

        LOGGER.info("COMPLETED")
        LOGGER.info("Saved all files to $targetDirectory")
        LOGGER.info("Process finished in ${Instant.now().toEpochMilli() - startTime}ms")
    }
}
fun main(args: Array<String>) {

    val sourceFile = File(args.getOrNull(0).also {
        if (it.isNullOrEmpty()) throw IllegalArgumentException("The source directory cannot be null or empty")
    }!!)
    val targetDirectory = File(args.getOrNull(1) ?: sourceFile.nameWithoutExtension)

    CurseforgeApiKey.getApiKey() ?: throw RuntimeException("No Value found for env CURSEFORGE_API_KEY")

    File("$targetDirectory/info.log").delete()
    System.setProperty("moddownloader.logFile.path", "$targetDirectory/info.log")
    Main().run(sourceFile, targetDirectory, true)
}