package de.joshi.modpackdownloader.parser

import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.util.getSubfile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files

class ManifestParser {
    private val LOGGER = KotlinLogging.logger { }

    fun getManifest(modpackFile: File): ManifestData {

        val manifestFile = modpackFile.getSubfile("manifest.json")
            ?: throw FileNotFoundException("No manifest.json file could be found in $modpackFile")
        LOGGER.info("Reading Manifest File from $manifestFile")
        return Json.decodeFromString(Files.readString(manifestFile.toPath()))
    }
}