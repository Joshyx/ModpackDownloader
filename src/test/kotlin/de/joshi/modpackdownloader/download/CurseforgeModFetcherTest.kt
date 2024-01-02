package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.models.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class CurseforgeModFetcherTest {

    @Test
    fun downloadUrlsForMods() {

        assertContentEquals(
            listOf(
                Url("https://edge.forgecdn.net/files/3857/643/architectury-1.32.66.jar") to ModCategory.MOD,
                Url("https://edge.forgecdn.net/files/3695/126/TConstruct-1.16.5-3.3.4.335.jar") to ModCategory.MOD,
                Url("https://edge.forgecdn.net/files/3969/615/upgradedcore-1.16.5-1.1.0.3-release.jar") to ModCategory.MOD,
            ).sortedBy { it.first.toString() },
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
                    ),
                    "",
                )
            ).toList().sortedBy { it.first.toString() }
        )
    }

    @Test
    fun getModInfo() = runTest {

        assertEquals(
            "upgradedcore-1.16.5-1.1.0.3-release.jar",
            CurseforgeModFetcher().fetchModInfo(ModData(566700, 3969615, true))?.name
        )
    }

    @Test
    fun getModInfoWithError() = runTest {

        assertNull (
            CurseforgeModFetcher().fetchModInfo(ModData(12343, 324134534, true))?.downloadURL
        )
    }

    @Test
    fun downloadFileInfo() = runTest {
        assertEquals(
            566700,
            CurseforgeModFetcher().fetchFileInfo(566700, 3969615).modId
        )
    }

    @Test
    fun getAlternativeDownloadUrl() {
        assertEquals(
            "https://edge.forgecdn.net/files/1234/567/myfile.jar",
            CurseforgeModFetcher().fetchAlternativeDownloadUrl(RawCurseForgeFileInfo(
                1234567,
                123,
                123,
                true,
                "myfile.jar",
                "myfile.jar",
                "https://edge.forgecdn.net/files/1234/567/myfile.jar",
                listOf("1.16.5"),
            ))
        )
    }

    @Test
    fun getManualDownloadUrl() = runTest {
        assertEquals(
            "https://www.curseforge.com/minecraft/mc-mods/upgraded-core/download/3969615",
            CurseforgeModFetcher().fetchManualDownloadUrl(566700, 3969615)
        )
    }
}