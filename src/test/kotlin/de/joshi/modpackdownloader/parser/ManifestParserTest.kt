package de.joshi.modpackdownloader.parser

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.FileNotFoundException

class ManifestParserTest {
    private val modpackFile = File("src/test/resources/modpack")

    @Test
    fun getManifestThrowsException() {
        assertThrows(FileNotFoundException::class.java) {
            ManifestParser().getManifest(File("sadf/dsajkvxcym/asdas"))
        }
    }
    @Test
    fun getManifestSuccessfully() {
        assertDoesNotThrow {
            ManifestParser().getManifest(modpackFile)
        }
    }
}