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
            ModDownloadUrlFetcher().getModInfo(419699, 3857643).contains("\"modId\":")
        )
    }

    @Test
    fun getModDownloadUrl() {

        Assertions.assertEquals(
            URL("https://edge.forgecdn.net/files/3857/643/architectury-1.32.66.jar"),
            ModDownloadUrlFetcher().getModDownloadUrl(
                "{\"data\":{\"id\":3857643,\"gameId\":432,\"modId\":419699,\"isAvailable\":true,\"displayName\":\"[Forge 1.16.5] v1.32.66\",\"fileName\":\"architectury-1.32.66.jar\",\"releaseType\":1,\"fileStatus\":4,\"hashes\":[{\"value\":\"e0f8a7569e033feea68e3d18e243ba13b36b0a9d\",\"algo\":1},{\"value\":\"5035bb619d94ef535c0094702ebe4f31\",\"algo\":2}],\"fileDate\":\"2022-07-03T10:19:44.217Z\",\"fileLength\":551890,\"downloadCount\":0,\"downloadUrl\":\"https://edge.forgecdn.net/files/3857/643/architectury-1.32.66.jar\",\"gameVersions\":[\"1.16.5\",\"Forge\",\"1.16.4\"],\"sortableGameVersions\":[{\"gameVersionName\":\"1.16.5\",\"gameVersionPadded\":\"0000000001.0000000016.0000000005\",\"gameVersion\":\"1.16.5\",\"gameVersionReleaseDate\":\"2021-01-15T14:14:48.91Z\",\"gameVersionTypeId\":70886},{\"gameVersionName\":\"Forge\",\"gameVersionPadded\":\"0\",\"gameVersion\":\"\",\"gameVersionReleaseDate\":\"2019-08-01T00:00:00Z\",\"gameVersionTypeId\":68441},{\"gameVersionName\":\"1.16.4\",\"gameVersionPadded\":\"0000000001.0000000016.0000000004\",\"gameVersion\":\"1.16.4\",\"gameVersionReleaseDate\":\"2020-11-02T18:40:51.49Z\",\"gameVersionTypeId\":70886}],\"dependencies\":[],\"alternateFileId\":0,\"isServerPack\":false,\"fileFingerprint\":6222768,\"modules\":[{\"name\":\"META-INF\",\"fingerprint\":1128907000},{\"name\":\"architectury-common.mixins.json\",\"fingerprint\":1305967297},{\"name\":\"architectury-common-refmap.json\",\"fingerprint\":1953518274},{\"name\":\"architectury.mixins.json\",\"fingerprint\":3532248734},{\"name\":\"icon.png\",\"fingerprint\":337711864},{\"name\":\"pack.mcmeta\",\"fingerprint\":1096803035},{\"name\":\"architectury-forge-refmap.json\",\"fingerprint\":1216577435},{\"name\":\"me\",\"fingerprint\":3891840592},{\"name\":\"architectury_inject_architectury_common_ca1d0ec5f9fb49e0884b2fd203d94bdc_10005706c4849fff4f8073260f102159e18547f7ff889545e7269c21c7cb8c21architectury13266devjar\",\"fingerprint\":2238169409}]}}"
            )
        )
    }
}