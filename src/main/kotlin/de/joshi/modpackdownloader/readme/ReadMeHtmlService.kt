package de.joshi.modpackdownloader.readme

import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.models.ReadMeInfo

class ReadMeHtmlService : ReadMeService {

    override fun createReadMe(manifestData: ManifestData): String {
        return ""
    }

    override fun getFileName(): String {
        return "README.html"
    }
}