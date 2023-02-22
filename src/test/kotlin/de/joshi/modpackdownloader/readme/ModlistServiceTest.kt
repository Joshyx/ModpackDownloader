package de.joshi.modpackdownloader.readme

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File

class ModlistServiceTest {

    @Test
    fun createModlist() {

        ModlistService().createModlist(
            File("src/test/resources/modpack/"),
            File("target/test/"),
            File("src/test/resources/modpack/overrides/")
        )
    }
}