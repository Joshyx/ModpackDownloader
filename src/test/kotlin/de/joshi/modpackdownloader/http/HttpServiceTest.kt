package de.joshi.modpackdownloader.http

import de.joshi.modpackdownloader.download.CurseforgeModFetcher
import de.joshi.modpackdownloader.download.FileDownloader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.net.URL
import java.nio.file.Files
import kotlin.test.assertContains

class HttpServiceTest {

    @Test
    fun getHttpBody() {
        assertContains(
            HttpService("").getHttpBody("https://example.com"),
            "<!DOCTYPE html>",
            true
        )
    }

    @Test
    fun downloadFile() {
        val path = File("test/downloaded/")

        path.deleteRecursively()
        FileDownloader().downloadModFiles(
            path, listOf(URL("https://edge.forgecdn.net/files/3857/643/architectury-1.32.66.jar"))
        )
        assertTrue(Files.exists(path.toPath()))
    }
}