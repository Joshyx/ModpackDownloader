package de.joshi.modpackdownloader.download

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URL
import java.nio.file.Files

class FileDownloaderTest {

    @Test
    fun downloadFiles() {
        val path = File("target/downloaded/files")

        path.deleteRecursively()
        FileDownloader().downloadFiles(
            path, listOf(URL("https://edge.forgecdn.net/files/3857/643/architectury-1.32.66.jar"))
        )
        Assertions.assertTrue(Files.exists(path.toPath()))
    }
}