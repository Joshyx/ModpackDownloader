package de.joshi.modpackdownloader.models

import java.net.URL

data class ModInfo (
    val name: String,
    val downloadURL: String,
    val required: Boolean,
    val downloadedInfoString: String
)
