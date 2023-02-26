package de.joshi.modpackdownloader.http

import de.joshi.modpackdownloader.auth.CurseforgeApiKey
import mu.KotlinLogging
import java.io.File
import java.lang.RuntimeException
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.exists

class HttpService(private val apiKey: String) {
    private val LOGGER = KotlinLogging.logger {  }

    fun getHttpBody(url: String): String {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(url))
            .header("x-api-key", apiKey)
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun downloadFile(fileUrl: URL, targetDirectory: File) {
        val fileName = fileUrl.file.substringAfterLast("/")
        val destinationFile = Paths.get("$targetDirectory/$fileName")
        LOGGER.info("Downloading file $fileName...")

        if (destinationFile.exists()) {
            LOGGER.info("File $fileName already exists")
            return
        }

        fileUrl.openStream().use {
            Files.copy(it, destinationFile, StandardCopyOption.REPLACE_EXISTING)
        }
        LOGGER.info("Saved $fileName to $destinationFile")
    }
}