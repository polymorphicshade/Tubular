package org.schabi.newpipe.settings

import org.schabi.newpipe.settings.export.BackupFileLocator
import org.schabi.newpipe.streams.io.StoredFileHelper
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
        0x10, 0x00, 0x01, 0x01, 0x00, 0x40, 0x20, 0x20, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x02,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x04
    )

    // JSON content for preferences
    private val jsonPrefs = """
        {
            "test_string": "one",
            "test_int": 12345,
            "test_double": 1.2345,
            "test_bool": true
        }
    """.trimIndent()

    // Regular serializable HashMap for preferences
    private fun createSerializedPrefs(): ByteArray {
        val prefs = HashMap<String, Any>()
        prefs["test_string"] = "one"
        prefs["test_int"] = 12345
        prefs["test_double"] = 1.2345
        prefs["test_bool"] = true

        val byteStream = ByteArrayOutputStream()
        ObjectOutputStream(byteStream).use { it.writeObject(prefs) }
        return byteStream.toByteArray()
    }

    // This class creates a serialization vulnerability by attempting to use a non-whitelisted class
    class VulnerableObject : Serializable {
        private val exec: String = "Runtime.getRuntime().exec('touch /tmp/pwned');"
    }

    // Create serialized content that would trigger a security exception due to using a non-whitelisted class
    private fun createVulnerableSerializedPrefs(): ByteArray {
        val prefs = HashMap<String, Any>()
        prefs["test_string"] = "one"
        prefs["test_int"] = 12345
        prefs["test_bool"] = true
        prefs["dangerous"] = VulnerableObject()

        val byteStream = ByteArrayOutputStream()
        ObjectOutputStream(byteStream).use { it.writeObject(prefs) }
        return byteStream.toByteArray()
    }

    /**
     * Creates a ZIP file with the requested content
     * @param includeDb Whether to include a database file in the ZIP
     * @param includeJson Whether to include JSON preferences in the ZIP
     * @param includeVulnerable Whether to include vulnerable serialized preferences in the ZIP
     * @param includeSerialized Whether to include normal serialized preferences in the ZIP
     * @return A temporary File containing the ZIP data
     */
    fun createZipFile(
        includeDb: Boolean = false,
        includeJson: Boolean = false,
        includeVulnerable: Boolean = false,
        includeSerialized: Boolean = false
    ): File {
        val tempFile = File.createTempFile("test_", ".zip")
        tempFile.deleteOnExit()

        ZipOutputStream(FileOutputStream(tempFile)).use { zipOut ->
            // Add database if requested
            if (includeDb) {
                zipOut.putNextEntry(ZipEntry(BackupFileLocator.FILE_NAME_DB))
                zipOut.write(dbContent)
                zipOut.closeEntry()
            }

            // Add serialized preferences if requested - FIX: Only add when explicitly requested
            if (includeVulnerable) {
                zipOut.putNextEntry(ZipEntry(BackupFileLocator.FILE_NAME_SERIALIZED_PREFS))
                zipOut.write(createVulnerableSerializedPrefs())
                zipOut.closeEntry()
            } else if (includeSerialized) {
                zipOut.putNextEntry(ZipEntry(BackupFileLocator.FILE_NAME_SERIALIZED_PREFS))
                zipOut.write(createSerializedPrefs())
                zipOut.closeEntry()
            }
            // REMOVED: Previous bug - was adding serialized prefs when includeJson was true

            // Add JSON preferences if requested
            if (includeJson) {
                zipOut.putNextEntry(ZipEntry(BackupFileLocator.FILE_NAME_JSON_PREFS))
                zipOut.write(jsonPrefs.toByteArray())
                zipOut.closeEntry()
            }
        }

        return tempFile
    }

    // Legacy method for backward compatibility with existing tests
    fun createJsonZip(): StoredFileHelper {
        val tempFile = createZipFile(includeJson = true)
        return ImportExportManagerTest.TestStoredFileHelper(tempFile)
    }

    // Legacy method for backward compatibility with existing tests
    fun createDbVulnserJsonZip(): StoredFileHelper {
        val tempFile = createZipFile(includeDb = true, includeVulnerable = true, includeJson = true)
        return ImportExportManagerTest.TestStoredFileHelper(tempFile)
    }
}
