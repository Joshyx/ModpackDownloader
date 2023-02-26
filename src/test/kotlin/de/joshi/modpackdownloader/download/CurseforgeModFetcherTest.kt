package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.models.MinecraftData
import de.joshi.modpackdownloader.models.ModData
import de.joshi.modpackdownloader.models.ModLoaderData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.net.URL
import kotlin.test.assertContains
import kotlin.test.assertContentEquals

class CurseforgeModFetcherTest {

    @Test
    fun downloadUrlsForMods() {

        assertContentEquals(
            listOf(
                URL("https://edge.forgecdn.net/files/3857/643/architectury-1.32.66.jar"),
                URL("https://edge.forgecdn.net/files/3695/126/TConstruct-1.16.5-3.3.4.335.jar"),
                URL("https://edge.forgecdn.net/files/3969/615/upgradedcore-1.16.5-1.1.0.3-release.jar"),
            ),
            CurseforgeModFetcher().fetchUrlsForMods(
                ManifestData(
                    MinecraftData("", listOf(ModLoaderData("", false))),
                    "",
                    0.0,
                    "",
                    "",
                    "",
                    listOf(
                        ModData(419699, 3857643, true),
                        ModData(74072, 3695126, true),
                        ModData(566700, 3969615, true),
                        ModData(567657, 3245, true),
                    ),
                    "",
                )
            )
        )
    }

    @Test
    fun getModInfo() {

        assertEquals(
            "upgradedcore-1.16.5-1.1.0.3-release.jar",
            CurseforgeModFetcher().fetchModInfo(ModData(566700, 3969615, true))?.name
        )
    }
    @Test
    fun getModInfoWithError() {

        assertNull(
            CurseforgeModFetcher().fetchModInfo(ModData(12343, 324134534, true))?.downloadURL
        )
    }
    @Test
    fun downloadFileInfo() {
        assert(!CurseforgeModFetcher().fetchFileInfo(566700, 3969615).isEmpty())
    }
    @Test
    fun getAlternativeDownloadUrl() {
        assertEquals(
            "https://edge.forgecdn.net/files/1234/567/myfile.jar",
            CurseforgeModFetcher().fetchAlternativeDownloadUrl(Json.decodeFromString("{\"id\":\"1234567\", \"fileName\":\"myfile.jar\"}"))
        )
    }
    @Test
    fun getManualDownloadUrl() {
        assertEquals(
            "https://www.curseforge.com/minecraft/mc-mods/upgraded-core/download/3969615",
            CurseforgeModFetcher().fetchManualDownloadUrl(566700, 3969615)
        )
    }
}