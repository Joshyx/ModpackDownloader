package de.joshi.modpackdownloader.overrides

import de.joshi.modpackdownloader.parser.ManifestParser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.nio.file.Files

class OverridesHandlerTest {

    @Test
    fun handleOverrides() {

        val inputFile = File("src/test/resources/modpack/overrides")
        val outputFile = File("target/test/overrides/")
        OverridesHandler().handleOverrides(inputFile, outputFile)
        assert(File(outputFile, "mods").exists())
        assert(File(outputFile, "resourcepacks").exists())
    }
}