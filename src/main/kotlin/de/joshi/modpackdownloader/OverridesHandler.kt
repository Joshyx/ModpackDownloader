package de.joshi.modpackdownloader

import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.util.getSubfolder
import mu.KotlinLogging
import java.io.File
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class OverridesHandler {
    private val LOGGER = KotlinLogging.logger {  }

    fun handleOverrides(modpackFile: File, manifest: ManifestData, targetDirectory: File) {
        val overridesFolder = modpackFile.getSubfolder(manifest.overrides) ?: return

        val configFolder = overridesFolder.getSubfolder("config")
        if (configFolder != null) {
            LOGGER.warn("There is an override for some config files in $configFolder that is not automatically dealt with by this program")
        }

        val modOverridesFolder = overridesFolder.getSubfolder("mods")
        if(modOverridesFolder != null) {
            modOverridesFolder.copyRecursively(targetDirectory, true)
            for (file in modOverridesFolder.list()) {
                LOGGER.info("Copying ${file} to $targetDirectory/$file")
            }
            LOGGER.info("Copied ${modOverridesFolder.list().size} mods into the target folder")
        }
    }
}

