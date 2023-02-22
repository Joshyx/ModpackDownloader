package de.joshi.modpackdownloader.readme

import mu.KotlinLogging
import java.io.File
import java.nio.file.Files

class ModlistService {
    private val LOGGER = KotlinLogging.logger {  }

    fun createModlist(sourceFile: File, targetFile: File, modOverrides: File?) {

        val lines = Files.readAllLines(File(sourceFile, "modlist.html").toPath())
        var firstIndex = lines.indexOf("</ul>")
        File(modOverrides, "mods").listFiles()?.forEachIndexed { i, file ->
            val modName = file.nameWithoutExtension.replaceFirstChar {it.uppercaseChar()}
            lines.add(
                firstIndex + i,
                "<li><a href=\"https://letmegooglethat.com/?q=$modName\">$modName</a></li>"
            )
            LOGGER.info { "Added ${file.name} to the modlist.html file" }
        }
        Files.writeString(
            File(targetFile, "modlist.html").toPath(),
            lines.map {
                if(it.contains("<ul>")) return@map "<ul>"
                if(it.contains("</ul>")) return@map "</ul>"
                it
            }.sortedWith { s1, s2 ->
                if(s1 =="<ul>") return@sortedWith -1
                if(s1 == "</ul>") return@sortedWith 1
                if(s2 =="<ul>") return@sortedWith 1
                if(s2 == "</ul>") return@sortedWith -1

                s1.substringAfter("\">").compareTo(s2.substringAfter("\">"), true)
            }.joinToString("\n")
        )
        LOGGER.info { "Wrote all mods to ${File(targetFile, "modlist.html")}" }
    }
}