package de.joshi.modpackdownloader.http

import de.joshi.modpackdownloader.Main.Companion.client
import de.joshi.modpackdownloader.models.FileData
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

class HttpService(private val apiKey: String) {

    private val filesDownloaded: MutableList<String> = ArrayList()

    fun getHttpBody(url: String): String {
        return runBlocking {
            val httpResponse: HttpResponse = client.get(url) {
                headers {
                    append("x-api-key", apiKey)
                }
            }
            return@runBlocking httpResponse.body<String>()
        }
    }

    suspend fun getFile(fileUrl: URL, targetDirectory: File, fileCount: Int): FileData = withContext(Dispatchers.IO) {
        val fileName: String = fileUrl.file.substringAfterLast("/")
        val destinationFile: Path = Paths.get("$targetDirectory/$fileName")

        val url: String = if (fileUrl.toString().contains(" ")) {
            fileUrl.toString().replace(" ", "%20")
        } else fileUrl.toString()

        val httpResponse: HttpResponse = client.get(url) {
            onDownload { min, max ->
                if ((min * 100 / max) > 90 && url !in filesDownloaded) filesDownloaded.add(url)
                when (val progress = filesDownloaded.size * 100 / fileCount) {
                    in 0..9     -> print(" - Downloading files [>.........] $progress% $fileName                                     \r")
                    in 10..19   -> print(" - Downloading files [#>........] $progress% $fileName                                     \r")
                    in 20..29   -> print(" - Downloading files [##>.......] $progress% $fileName                                     \r")
                    in 30..39   -> print(" - Downloading files [###>......] $progress% $fileName                                     \r")
                    in 40..49   -> print(" - Downloading files [####>.....] $progress% $fileName                                     \r")
                    in 50..59   -> print(" - Downloading files [#####>....] $progress% $fileName                                     \r")
                    in 60..69   -> print(" - Downloading files [######>...] $progress% $fileName                                     \r")
                    in 70..79   -> print(" - Downloading files [#######>..] $progress% $fileName                                     \r")
                    in 80..89   -> print(" - Downloading files [########>.] $progress% $fileName                                     \r")
                    in 90..99   -> print(" - Downloading files [#########>] $progress% $fileName                                     \r")
                    100         -> print(" - Downloading files [##########] $progress% Done!                                         \r")
                }
            }
        }
        val responseBody: ByteArray = httpResponse.body()

        return@withContext FileData(fileName, responseBody, destinationFile)
    }
}