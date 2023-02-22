package de.joshi.modpackdownloader

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileNotFoundException
import kotlin.test.assertContains

class ApplicationIT {

    @Test
    fun runAppSuccessfully() {

        val outputDirectory = "target/test/downloaded/files"
        assertDoesNotThrow {
            main(arrayOf("src/test/resources/modpack.zip", outputDirectory))
        }
        assertContains(
            File(outputDirectory).list() as Array<String>,
            "mods"
        )
        assertContains(
            File(outputDirectory).list() as Array<String>,
            "originalModPack"
        )
        assertContains(
            File(outputDirectory).list() as Array<String>,
            "README.md"
        )
    }

    @Test
    fun runAppThrowsException() {
        assertThrows(IllegalArgumentException::class.java) {
            main(arrayOf())
        }
        assertThrows(IllegalArgumentException::class.java) {
            main(arrayOf("", "target/downloaded/files"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            main(arrayOf("src/test/resources/modpack.", ""))
        }
        assertThrows(FileNotFoundException::class.java) {
            main(arrayOf("src/test/resources/modpack.z", "target/dowloaded/files"))
        }
    }
}