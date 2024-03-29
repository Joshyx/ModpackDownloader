package de.joshi.modpackdownloader.http

import de.joshi.modpackdownloader.download.FileDownloader
import de.joshi.modpackdownloader.models.ModCategory
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import kotlin.test.assertContains

class HttpServiceTest {

    @Test
    fun getHttpBody() = runTest {
        assertContains(
            HttpService.getHttpBody("https://example.com"), "<!DOCTYPE html>", true
        )
    }

    @Test
    fun getFile() {
        val path = File("target/test/downloaded/")

        path.deleteRecursively()
        FileDownloader().downloadModFiles(
            path, mapOf(Url("https://edge.forgecdn.net/files/3857/643/architectury-1.32.66.jar") to ModCategory.MOD)
        )
        assertTrue(Files.exists(path.toPath()))

        path.deleteRecursively()
    }
}