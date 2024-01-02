package de.joshi.modpackdownloader.readme

import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.util.getOrCreateSubfolder
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.nio.file.Files

interface ReadMeService {
    private val LOGGER: KLogger
        get() = KotlinLogging.logger { }

    fun saveReadMe(targetDirectory: File, manifestData: ManifestData) {
        Files.writeString(targetDirectory.getOrCreateSubfolder(getFileName()).toPath(), createReadMe(manifestData))
        LOGGER.info { "Saved the modpack info to ${File(targetDirectory, getFileName())}" }
    }

    fun createReadMe(manifestData: ManifestData): String

    fun getFileName(): String
}