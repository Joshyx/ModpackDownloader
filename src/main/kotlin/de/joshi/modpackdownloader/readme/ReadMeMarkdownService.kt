package de.joshi.modpackdownloader.readme

import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.models.ModLoaderData
import de.joshi.modpackdownloader.models.ReadMeInfo

class ReadMeMarkdownService : ReadMeService {
    override fun createReadMe(manifestData: ManifestData): String {
        return """
# Modpack Info
## General
${manifestData.name} ${manifestData.version} by ${manifestData.author.ifBlank { "an unknown author" }}

Minecraft Version: ${manifestData.minecraft.version}

Mod Loader: ${manifestData.minecraft.modLoaders.filter { it.primary }.getOrElse(0) { ModLoaderData("unknown", true) }.id}

Mod Count: ${manifestData.files.size}

## Errors
${ReadMeInfo.errors.joinToString("\n\n")}
        """.trimIndent()
    }

    override fun getFileName(): String {
        return "README.md"
    }
}