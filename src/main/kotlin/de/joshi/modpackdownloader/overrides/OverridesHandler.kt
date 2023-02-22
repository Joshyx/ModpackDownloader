package de.joshi.modpackdownloader.overrides

import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.util.getOrCreateSubfolder
import de.joshi.modpackdownloader.util.getSubfolder
import mu.KotlinLogging
import java.io.File

class OverridesHandler {
    private val LOGGER = KotlinLogging.logger { }

    fun handleOverrides(overridesFolder: File, targetDirectory: File) {

        val foldersToCopy = listOf("mods", "shaderpacks", "resourcepacks")

        overridesFolder.listFiles {
                file -> file.isDirectory
        }?.forEach { file ->
            if(foldersToCopy.any { it == file.name }) {
                file.copyRecursively(targetDirectory.getOrCreateSubfolder(file.name), true)
                for (copiedFile in file.list()!!) {
                    LOGGER.info("Copying $file/$copiedFile to $targetDirectory/${file.name}/$copiedFile")
                }
                LOGGER.info("Copied ${file.list()!!.size} file${if(file.list().size == 1) "" else "s"} into $targetDirectory/${file.name}")

            } else {
                LOGGER.warn("There is an override for some files in $file that is not automatically dealt with by this program")
            }
        }
    }

    fun getOverridesFolder(modpackFile: File, manifest: ManifestData): File? {
        return modpackFile.getSubfolder(manifest.overrides)
    }
}