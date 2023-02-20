package de.joshi.modpackdownloader.util

import java.io.File

fun File.getSubfile(subFileName: String, ignoreCase: Boolean = false): File? {
    return this.listFiles { file ->
        file.name.equals(subFileName, ignoreCase)
    }?.getOrNull(0)
}
fun File.getSubfolder(subFileName: String, ignoreCase: Boolean = false): File? {
    return this.listFiles { file ->
        file.isDirectory && file.name.equals(subFileName, ignoreCase)
    }?.getOrNull(0)
}