package de.joshi.modpackdownloader.models

import kotlinx.serialization.Serializable

@Serializable
data class RawCurseForgeModInfo (
    val classId: Int,
    val slug: String
)

@Serializable
data class RawCurseForgeModInfoWrapper (
    val data: RawCurseForgeModInfo
)