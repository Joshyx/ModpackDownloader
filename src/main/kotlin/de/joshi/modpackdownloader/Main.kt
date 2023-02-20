package de.joshi.modpackdownloader

import de.joshi.modpackdownloader.download.FileDownloader
import de.joshi.modpackdownloader.download.ModDownloadUrlFetcher
import de.joshi.modpackdownloader.models.ReadMeInfo
import de.joshi.modpackdownloader.parser.ManifestParser
import de.joshi.modpackdownloader.readme.ReadMeMarkdownService
import de.joshi.modpackdownloader.util.getOrCreateSubfolder
import de.joshi.modpackdownloader.util.getSubfolder
import de.joshi.modpackdownloader.zip.UnzipService
import mu.KotlinLogging
import java.io.File
import java.time.Instant

private val LOGGER = KotlinLogging.logger {  }

fun main(args: Array<String>) {
    val startTime = Instant.now().toEpochMilli()

    val sourceFile = File(args.getOrNull(0) ?: throw IllegalArgumentException("The source directory cannot be null or empty"))
    val targetDirectory = File(args.getOrNull(1) ?: throw IllegalArgumentException("The target directory cannot be null or empty"))

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
    LOGGER.info("Saved all mods to $targetDirectory/mods")
    LOGGER.info("Process finished in ${Instant.now().toEpochMilli() - startTime}ms")
}