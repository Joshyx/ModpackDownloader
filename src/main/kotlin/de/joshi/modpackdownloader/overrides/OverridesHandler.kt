package de.joshi.modpackdownloader.overrides

import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.util.getOrCreateSubfolder
import de.joshi.modpackdownloader.util.getSubfolder
import mu.KotlinLogging
import java.io.File

class OverridesHandler {
    private val LOGGER = KotlinLogging.logger { }

    fun handleOverrides(modpackFile: File, manifest: ManifestData, targetDirectory: File) {
        val overridesFolder = modpackFile.getSubfolder(manifest.overrides) ?: return
        val foldersToCopy = listOf("mods", "shaderpacks", "resourcepacks")

        overridesFolder.listFiles { file ->
            foldersToCopy.none { it == file.name } &&
            file.isDirectory
        }?.forEach {
            LOGGER.warn("There is an override for some files in $it that is not automatically dealt with by this program")
        }

        overridesFolder.listFiles { file ->
            foldersToCopy.any { it == file.name } &&
            file.isDirectory
        }?.forEach {
            it.copyRecursively(targetDirectory.getOrCreateSubfolder(it.name), true)
            for (file in it.list()!!) {
                LOGGER.info("Copying $it/$file to $targetDirectory/${it.name}/$file")
            }
            LOGGER.info("Copied ${it.list()!!.size} file${if(it.list().size == 1) "" else "s"} into $targetDirectory/${it.name}")
        }
    }
}