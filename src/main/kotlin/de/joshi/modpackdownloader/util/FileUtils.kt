package de.joshi.modpackdownloader.util

import java.io.File

fun File.getSubfolder(subFileName: String): File? {
    return File(this, subFileName).takeIf { file -> file.exists() }
}

fun File.getOrCreateSubfolder(subFileName: String): File {
    this.mkdirs()
    val file = File(this, subFileName)
    file.createNewFile()
    return file
}