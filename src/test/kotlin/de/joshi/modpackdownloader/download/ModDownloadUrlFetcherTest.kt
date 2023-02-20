package de.joshi.modpackdownloader.download

import de.joshi.modpackdownloader.models.ManifestData
import de.joshi.modpackdownloader.models.MinecraftData
import de.joshi.modpackdownloader.models.ModData
import de.joshi.modpackdownloader.models.ModLoaderData
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.net.URL
import kotlin.test.assertContentEquals

class ModDownloadUrlFetcherTest {

    @Test
    fun downloadUrlsForMods() {

        assertContentEquals(
            listOf(
                URL("https://edge.forgecdn.net/files/3857/643/architectury-1.32.66.jar"),
                URL("https://edge.forgecdn.net/files/3695/126/TConstruct-1.16.5-3.3.4.335.jar"),
                URL("https://edge.forgecdn.net/files/3969/615/upgradedcore-1.16.5-1.1.0.3-release.jar"),
            ),
            ModDownloadUrlFetcher().downloadUrlsForMods(
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
            )
        )
    }

    @Test
    fun getModInfo() {

        Assertions.assertTrue(
            ModDownloadUrlFetcher().getModInfo(ModData(566700, 3969615, true)).name == "upgradedcore-1.16.5-1.1.0.3-release.jar"
        )
    }
}