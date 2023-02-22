package de.joshi.modpackdownloader.parser

import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.models.ModLoaderData
import de.joshi.modpackdownloader.util.getSubfolder
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files

class ManifestParser {
    private val LOGGER = KotlinLogging.logger { }

    fun getManifest(modpackFile: File): ManifestData {

        val manifestFile = modpackFile.getSubfolder("manifest.json")
            ?: throw FileNotFoundException("No manifest.json file could be found in $modpackFile")

        LOGGER.info("Reading Manifest File from $manifestFile")
        val manifestData = Json.decodeFromString<ManifestData>(Files.readString(manifestFile.toPath()))
        LOGGER.info("Manifest Info:")
        LOGGER.info("Modpack: ${manifestData.name} ${manifestData.version} by ${manifestData.author.ifBlank { "an unknown author" }}")
        LOGGER.info("Minecraft Version: ${manifestData.minecraft.version}, ModLoader: ${manifestData.minecraft.modLoaders.filter { it.primary }.getOrElse(0) { ModLoaderData("unknown", true) }.id}")
        LOGGER.info("Containing ${manifestData.files.size} mods")
        return manifestData
    }
}