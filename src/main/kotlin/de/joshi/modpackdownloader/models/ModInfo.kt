package de.joshi.modpackdownloader.models

import io.ktor.http.*

data class ModInfo(
    val name: String,
    val downloadURL: Url?,
    val required: Boolean,
    val downloadedInfo: RawCurseForgeFileInfo,
    val category: ModCategory
)
