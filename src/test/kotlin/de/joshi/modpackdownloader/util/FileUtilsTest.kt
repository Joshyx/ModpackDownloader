package de.joshi.modpackdownloader.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class FileUtilsTest {

    @Test
    fun getSubfolder() {
        assertEquals(File("src/main"), File("src").getSubfolder("main"))
    }

    @Test
    fun getOrCreateSubfolder() {
        assertEquals(File("src/main"), File("src").getSubfolder("main"))
        assert(File("target/test/asdjifvofxcvkmsdgf").getOrCreateSubfolder("asddsf").exists())
    }
}