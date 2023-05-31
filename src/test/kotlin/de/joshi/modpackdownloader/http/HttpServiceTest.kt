package de.joshi.modpackdownloader.http

import de.joshi.modpackdownloader.download.FileDownloader
import de.joshi.modpackdownloader.models.ModCategory
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URL
import java.nio.file.Files
import kotlin.test.assertContains

class HttpServiceTest {

    @Test
    fun getHttpBody() {
        assertContains(
            HttpService.getHttpBody("https://example.com"), "<!DOCTYPE html>", true
        )
    }

    @Test
    fun getFile() {
        val path = File("test/downloaded/")

        path.deleteRecursively()
        FileDownloader().downloadModFiles(
            path, mapOf(URL("https://edge.forgecdn.net/files/3857/643/architectury-1.32.66.jar") to ModCategory.MOD)
        )
        assertTrue(Files.exists(path.toPath()))
    }
}