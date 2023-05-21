package de.joshi.modpackdownloader.models

import kotlinx.serialization.Serializable

@Serializable
data class ManifestData(
    val minecraft: MinecraftData,
    val manifestType: String,
    val manifestVersion: Double,
    val name: String,
    val version: String,
    val author: String,
    val files: List<ModData>,
    val overrides: String,
)

@Serializable
data class MinecraftData(
    val version: String,
    val modLoaders: List<ModLoaderData>,
)

@Serializable
data class ModLoaderData(
    val id: String,
    val primary: Boolean,
)

@Serializable
data class ModData(
    val projectID: Int,
    val fileID: Int,
    val required: Boolean,
)