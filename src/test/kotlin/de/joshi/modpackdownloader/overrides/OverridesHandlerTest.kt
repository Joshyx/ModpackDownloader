package de.joshi.modpackdownloader.overrides

import org.junit.jupiter.api.Test
import java.io.File

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