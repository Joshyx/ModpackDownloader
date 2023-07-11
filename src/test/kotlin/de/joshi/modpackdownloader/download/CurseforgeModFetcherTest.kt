package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.models.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CurseforgeModFetcherTest {

    @Test
    fun downloadUrlsForMods() {

// Todo: Fix this test
//        assertContentEquals(
//            setOf(
//                URL("https://edge.forgecdn.net/files/3857/643/architectury-1.32.66.jar") to ModCategory.MOD,
//                URL("https://edge.forgecdn.net/files/2668/311/%C2%A7c%C2%A7lCream%20%C2%A77Cuisine%20Resource%20Pack%20-%200.5.0.zip") to ModCategory.RESOURCE_PACK,
//                URL("https://edge.forgecdn.net/files/3969/615/upgradedcore-1.16.5-1.1.0.3-release.jar") to ModCategory.MOD,
//            ), CurseforgeModFetcher().fetchUrlsForMods(
//                ManifestData(
//                    MinecraftData("", listOf(ModLoaderData("", false))),
//                    "",
//                    0.0,
//                    "",
//                    "",
//                    "",
//                    listOf(
//                        ModData(419699, 3857643, true),
//                        ModData(74072, 3695126, true),
//                        ModData(566700, 3969615, true),
//                        ModData(567657, 3245, true),
//                    ),
//                    "",
//                )
//            )
//        )
    }

    @Test
    suspend fun getModInfo() {

        assertEquals(
            "upgradedcore-1.16.5-1.1.0.3-release.jar",
            CurseforgeModFetcher().fetchModInfo(ModData(566700, 3969615, true))?.name
        )
    }

    @Test
    suspend fun getModInfoWithError() {

        assertNull(
            CurseforgeModFetcher().fetchModInfo(ModData(12343, 324134534, true))?.downloadURL
        )
    }

    @Test
    suspend fun downloadFileInfo() {
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
    suspend fun getManualDownloadUrl() {
        assertEquals(
            "https://www.curseforge.com/minecraft/mc-mods/upgraded-core/download/3969615",
            CurseforgeModFetcher().fetchManualDownloadUrl(566700, 3969615)
        )
    }
}