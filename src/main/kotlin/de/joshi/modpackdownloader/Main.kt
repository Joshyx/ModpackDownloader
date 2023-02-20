package de.joshi.modpackdownloader

import de.joshi.modpackdownloader.download.FileDownloader
import de.joshi.modpackdownloader.download.ModDownloadUrlFetcher
import de.joshi.modpackdownloader.parser.ManifestParser
import mu.KotlinLogging
import java.io.File
import java.time.Instant

private val LOGGER = KotlinLogging.logger {  }

fun main(args: Array<String>) {
    val startTime = Instant.now().toEpochMilli()

    val sourceDirectory = File(args.getOrNull(0) ?: throw IllegalArgumentException("The source directory cannot be null or empty"))
    val targetDirectory = File(args.getOrNull(1) ?: throw IllegalArgumentException("The target directory cannot be null or empty"))

    val manifest = ManifestParser().getManifest(sourceDirectory)

    val modListDownloadStartTime = Instant.now().toEpochMilli()
    val modList = ModDownloadUrlFetcher().downloadUrlsForMods(manifest)
    LOGGER.info("Downloaded mod URLs in ${Instant.now().toEpochMilli() - modListDownloadStartTime}ms")

    FileDownloader().downloadFiles(
        targetDirectory,
        modList,
        false
    )

    OverridesHandler().handleOverrides(sourceDirectory, manifest, targetDirectory)

    LOGGER.info("COMPLETED")
    LOGGER.info("Saved all mods to $targetDirectory")
    LOGGER.info("Process finished in ${Instant.now().toEpochMilli() - startTime}ms")
}