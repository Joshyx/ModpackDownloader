package de.joshi.modpackdownloader

import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import de.joshi.modpackdownloader.download.FileDownloader
import de.joshi.modpackdownloader.download.ModDownloadUrlFetcher
import de.joshi.modpackdownloader.models.ReadMeInfo
import de.joshi.modpackdownloader.overrides.OverridesHandler
import de.joshi.modpackdownloader.parser.ManifestParser
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

    fun run(sourceFile: File, targetDirectory: File) {
        val startTime = Instant.now().toEpochMilli()

        val sourceDirectory = UnzipService().unzip(sourceFile, File(targetDirectory, "originalModPack"))

        val manifest = ManifestParser().getManifest(sourceDirectory)

        val modListDownloadStartTime = Instant.now().toEpochMilli()
        val modList = ModDownloadUrlFetcher().downloadUrlsForMods(manifest, false)
        LOGGER.info("Downloaded mod URLs in ${Instant.now().toEpochMilli() - modListDownloadStartTime}ms")

        FileDownloader().downloadFiles(
            File(targetDirectory, "mods"),
            modList,
            false
        )

        OverridesHandler().handleOverrides(sourceDirectory, manifest, targetDirectory)

        ReadMeMarkdownService().saveReadMe(targetDirectory, manifest)

        LOGGER.info("COMPLETED")
        LOGGER.info("Saved all files to $targetDirectory")
        LOGGER.info("Process finished in ${Instant.now().toEpochMilli() - startTime}ms")
    }
}
fun main(args: Array<String>) {
    val sourceFile = File(args.getOrNull(0) ?: throw IllegalArgumentException("The source directory cannot be null or empty"))
    val targetDirectory = File(args.getOrNull(1) ?: throw IllegalArgumentException("The target directory cannot be null or empty"))

    System.setProperty("moddownloader.logFile.path", "$targetDirectory/info.log")
    Main().run(sourceFile, targetDirectory)
}