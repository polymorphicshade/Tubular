package org.schabi.newpipe.settings

import org.mockito.Mockito
import org.schabi.newpipe.streams.io.SharpStream
import org.schabi.newpipe.streams.io.StoredFileHelper
import java.io.ByteArrayInputStream
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

    // File paths in the zip
    private const val DB_PATH = "settings/newpipe.db"
    private const val SERIALIZED_PREFS_PATH = "settings/newpipe.settings"
    private const val JSON_PREFS_PATH = "settings/newpipe.json"

    // JSON content for preferences
    private const val jsonPrefs = """
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

    // Helper to create a temporary zip file with the given content
    private fun createZipFile(
        includeDb: Boolean = false,
        includeSerialized: Serialized = Serialized.NONE,
        includeJson: Boolean = false
    ): File {
        val tempFile = File.createTempFile("test_", ".zip")
        tempFile.deleteOnExit()

        ZipOutputStream(FileOutputStream(tempFile)).use { zipOut ->
            // Add database if requested
            if (includeDb) {
                zipOut.putNextEntry(ZipEntry(DB_PATH))
                zipOut.write(dbContent)
                zipOut.closeEntry()
            }

            // Add serialized preferences if requested
            if (includeSerialized != Serialized.NONE) {
                zipOut.putNextEntry(ZipEntry(SERIALIZED_PREFS_PATH))
                when (includeSerialized) {
                    Serialized.NORMAL -> zipOut.write(createSerializedPrefs())
                    Serialized.VULNERABLE -> zipOut.write(createVulnerableSerializedPrefs())
                    else -> {} // Nothing to do for NONE
                }
                zipOut.closeEntry()
            }

            // Add JSON preferences if requested
            if (includeJson) {
                zipOut.putNextEntry(ZipEntry(JSON_PREFS_PATH))
                zipOut.write(jsonPrefs.toByteArray())
                zipOut.closeEntry()
            }
        }

        return tempFile
    }

    /**
     * Helper enum for serialized preferences type
     */
    private enum class Serialized {
        NONE,
        NORMAL,
        VULNERABLE
    }

    /**
     * Creates a mock StoredFileHelper with a provided zip file input stream
     * * @param zipFile The zip file to use as input
     * @return A mocked StoredFileHelper that will return the zip file's content as a SharpStream
     */
    fun createMockStoredFileHelper(zipFile: File): StoredFileHelper {
        val mockHelper = Mockito.mock(StoredFileHelper::class.java)
        val mockStream = Mockito.mock(SharpStream::class.java)

        // Set up the mock stream to read from the file
        val fileBytes = zipFile.readBytes()
        val inputStream = ByteArrayInputStream(fileBytes)

        // When read() is called, delegate to the ByteArrayInputStream
        Mockito.`when`(mockStream.read()).thenAnswer { inputStream.read() }
        Mockito.`when`(mockStream.read(Mockito.any(ByteArray::class.java))).thenAnswer { inputStream.read(it.getArgument(0) as ByteArray) }
        Mockito.`when`(
            mockStream.read(
                Mockito.any(ByteArray::class.java), Mockito.anyInt(), Mockito.anyInt()
            )
        ).thenAnswer {
            val buffer = it.getArgument(0) as ByteArray
            val offset = it.getArgument(1) as Int
            val length = it.getArgument(2) as Int
            inputStream.read(buffer, offset, length)
        }

        // Set up the mock helper
        Mockito.`when`(mockHelper.stream).thenReturn(mockStream)
        return mockHelper
    }

    // Functions to create different test files

    fun createDbZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = true, includeSerialized = Serialized.NONE, includeJson = false)
    )

    fun createRootDbZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = true, includeSerialized = Serialized.NONE, includeJson = false)
    )

    fun createNoDbZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = false, includeSerialized = Serialized.NONE, includeJson = false)
    )

    fun createJsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = false, includeSerialized = Serialized.NONE, includeJson = true)
    )

    // Functions to create different combinations of zip files
    fun createDbSerJsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = true, includeSerialized = Serialized.NORMAL, includeJson = true)
    )

    fun createDbSerNojsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = true, includeSerialized = Serialized.NORMAL, includeJson = false)
    )

    fun createDbVulnserJsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = true, includeSerialized = Serialized.VULNERABLE, includeJson = true)
    )

    fun createDbVulnserNojsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = true, includeSerialized = Serialized.VULNERABLE, includeJson = false)
    )

    fun createDbNoserJsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = true, includeSerialized = Serialized.NONE, includeJson = true)
    )

    fun createDbNoserNojsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = true, includeSerialized = Serialized.NONE, includeJson = false)
    )

    fun createNodbSerJsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = false, includeSerialized = Serialized.NORMAL, includeJson = true)
    )

    fun createNodbSerNojsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = false, includeSerialized = Serialized.NORMAL, includeJson = false)
    )

    fun createNodbVulnserJsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = false, includeSerialized = Serialized.VULNERABLE, includeJson = true)
    )

    fun createNodbVulnserNojsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = false, includeSerialized = Serialized.VULNERABLE, includeJson = false)
    )

    fun createNodbNoserJsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = false, includeSerialized = Serialized.NONE, includeJson = true)
    )

    fun createNodbNoserNojsonZip(): StoredFileHelper = createMockStoredFileHelper(
        createZipFile(includeDb = false, includeSerialized = Serialized.NONE, includeJson = false)
    )
}
