package de.joshi.modpackdownloader.models

import java.io.File
import java.nio.file.Path

data class FileData(
    val name: String,
    val responseBody: ByteArray,
    val destination: Path,
    val parentDirectory: File
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileData

        if (name != other.name) return false
        if (!responseBody.contentEquals(other.responseBody)) return false
        if (destination != other.destination) return false
        if (parentDirectory != other.parentDirectory) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + responseBody.contentHashCode()
        result = 31 * result + destination.hashCode()
        result = 31 * result + parentDirectory.hashCode()
        return result
    }
}
