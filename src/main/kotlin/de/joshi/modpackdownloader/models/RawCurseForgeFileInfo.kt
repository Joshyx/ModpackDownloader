package de.joshi.modpackdownloader.models

import kotlinx.serialization.Serializable

@Serializable
data class RawCurseForgeFileInfo(
    val id: Int,
    val gameId: Int,
    val modId: Int,
    val isAvailable: Boolean,
    val displayName: String,
    val fileName: String,
    val downloadUrl: String,
    val gameVersions: List<String>,
)

@Serializable
data class RawCurseForgeFileInfoWrapper(
    val data: RawCurseForgeFileInfo
)