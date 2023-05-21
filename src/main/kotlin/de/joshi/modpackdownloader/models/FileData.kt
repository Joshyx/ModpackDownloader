package de.joshi.modpackdownloader.models

import java.nio.file.Path

data class FileData(
    val fileName: String,
    val responseBody: ByteArray,
    val destinationFile: Path
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileData

        if (fileName != other.fileName) return false
        if (!responseBody.contentEquals(other.responseBody)) return false
        if (destinationFile != other.destinationFile) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + responseBody.contentHashCode()
        result = 31 * result + destinationFile.hashCode()
        return result
    }
}
