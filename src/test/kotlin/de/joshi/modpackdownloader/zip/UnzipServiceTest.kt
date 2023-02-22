package de.joshi.modpackdownloader.zip

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.util.zip.ZipFile

class UnzipServiceTest {

    @Test
    fun unzip() {

        val outputPath = File("target/test/zip")
        val zipFile = File("src/test/resources/modpack.zip")
        UnzipService().unzip(zipFile, outputPath)

        assertTrue(outputPath.exists())
    }
}