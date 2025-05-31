package org.schabi.newpipe.settings

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.HashMap
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Helper class to generate test data on the fly instead of relying on resource files.
 * This eliminates file loading issues on different platforms.
 */
object TestData {
    // More realistic binary content for a database (simulating SQLite header and some data)
    private val dbContent = byteArrayOf(
        0x53, 0x51, 0x4C, 0x69, 0x74, 0x65, 0x20, 0x66, 0x6F, 0x72, 0x6D, 0x61, 0x74, 0x20, 0x33, 0x00,
        0x10, 0x00, 0x01, 0x01, 0x00, 0x40, 0x20, 0x20, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04
    ) + "NewPipe Test Database Content".toByteArray()

    // Sample preferences data as a map that implements Serializable
    private val prefsData = HashMap<String, Any>().apply {
        put("theme", "dark")
        put("video_quality", "720p")
        put("use_external_player", true)
        put("search_history_enabled", true)
        put("background_play", true)
        put("download_path", "/storage/videos")
        put("max_search_results", 50)
        put("auto_play", false)
        put("notification_enabled", true)
        put("preferred_language", "en")
    }

    // Class to create a malicious serialized data that would be rejected by PreferencesObjectInputStream
    private class SerializedClassNameRef : Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }

        // This class name is not in the whitelist and will be rejected
        private val className = "org.example.MaliciousClass"
    }

    // This class will not be in the whitelist and should cause a ClassNotFoundException
    private class MaliciousData : Serializable {
        companion object {
            private const val serialVersionUID = 2L
        }

        // Some data for evidence it's the right class
        private val data = "malicious payload"
    }

    /**
     * Creates a test database file
     */
    fun createDbFile(): File {
        val file = File.createTempFile("newpipe_test_", ".db")
        file.deleteOnExit()

        file.writeBytes(dbContent)
        return file
    }

    /**
     * Creates a zip file with the specified contents
     */
    private fun createZipFile(
        hasDb: Boolean,
        hasSer: Boolean,
        isVulnerableSer: Boolean,
        hasJson: Boolean
    ): File {
        val file = File.createTempFile("newpipe_test_", ".zip")
        file.deleteOnExit()

        ZipOutputStream(FileOutputStream(file)).use { zipOut ->
            // Add database if needed
            if (hasDb) {
                val entry = ZipEntry("newpipe.db")
                zipOut.putNextEntry(entry)
                zipOut.write(dbContent)
                zipOut.closeEntry()
            }

            // Add serialized preferences if needed
            if (hasSer) {
                val entry = ZipEntry("newpipe.settings")
                zipOut.putNextEntry(entry)

                // Create serialized data
                ByteArrayOutputStream().use { byteOut ->
                    if (isVulnerableSer) {
                        // Create vulnerable serialized data that will trigger the security check
                        ObjectOutputStream(byteOut).use { objectOut ->
                            // This is the key change - we need to serialize a class that's not in the whitelist
                            // but in a way that will trigger PreferencesObjectInputStream.resolveClass()
                            val maliciousMap = HashMap<String, Any>()
                            maliciousMap["malicious"] = MaliciousData() // This class is not in the whitelist
                            objectOut.writeObject(maliciousMap)
                        }
                    } else {
                        // Create normal serialized data with HashMap (which is Serializable and in the whitelist)
                        ObjectOutputStream(byteOut).use { objectOut ->
                            objectOut.writeObject(prefsData)
                        }
                    }
                    zipOut.write(byteOut.toByteArray())
                }
                zipOut.closeEntry()
            }

            // Add JSON preferences if needed
            if (hasJson) {
                val entry = ZipEntry("preferences.json")
                zipOut.putNextEntry(entry)
                val jsonContent = """
                    {
                        "theme": "dark",
                        "video_quality": "720p",
                        "use_external_player": true,
                        "search_history_enabled": true,
                        "background_play": true,
                        "download_path": "/storage/videos",
                        "max_search_results": 50,
                        "auto_play": false,
                        "notification_enabled": true,
                        "preferred_language": "en"
                    }
                """.trimIndent()
                zipOut.write(jsonContent.toByteArray())
                zipOut.closeEntry()
            }
        }

        return file
    }

    // Helper methods for creating specific test files
    fun createDbSerJsonZip(): File = createZipFile(true, true, false, true)
    fun createDbSerNojsonZip(): File = createZipFile(true, true, false, false)
    fun createDbVulnserJsonZip(): File = createZipFile(true, true, true, true)
    fun createDbVulnserNojsonZip(): File = createZipFile(true, true, true, false)
    fun createDbNoserJsonZip(): File = createZipFile(true, false, false, true)
    fun createDbNoserNojsonZip(): File = createZipFile(true, false, false, false)
    fun createNodbSerJsonZip(): File = createZipFile(false, true, false, true)
    fun createNodbSerNojsonZip(): File = createZipFile(false, true, false, false)
    fun createNodbVulnserJsonZip(): File = createZipFile(false, true, true, true)
    fun createNodbVulnserNojsonZip(): File = createZipFile(false, true, true, false)
    fun createNodbNoserJsonZip(): File = createZipFile(false, false, false, true)
    fun createNodbNoserNojsonZip(): File = createZipFile(false, false, false, false)
}
