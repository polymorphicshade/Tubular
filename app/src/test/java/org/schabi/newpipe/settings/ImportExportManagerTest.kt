package org.schabi.newpipe.settings

import android.content.SharedPreferences
import com.grack.nanojson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.anyBoolean
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner.Silent
import org.schabi.newpipe.settings.export.BackupFileLocator
import org.schabi.newpipe.settings.export.ImportExportManager
import org.schabi.newpipe.streams.io.SharpStream
import org.schabi.newpipe.streams.io.StoredFileHelper
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipFile

@RunWith(Silent::class)
class ImportExportManagerTest {

    /**
     * Custom StoredFileHelper implementation that doesn't use the problematic FileStream class
     * but instead uses Java's standard FileInputStream, which is more reliable in tests.
     */
    internal class TestStoredFileHelper(private val file: File) : StoredFileHelper(
        null,
        file.name,
        "application/zip",
        null
    ) {
        // Initialize source field with a URI string
        init {
            // Create a proper URI that works on all platforms
            val path: Path = Paths.get(file.absolutePath)
            source = path.toUri().toString()
        }

        override fun getStream(): SharpStream {
            // Create an adapter that wraps BufferedInputStream and implements SharpStream
            return object : SharpStream() {
                private val stream = BufferedInputStream(FileInputStream(file))
                private var closed = false

                override fun read(): Int = stream.read()

                override fun read(buffer: ByteArray): Int = stream.read(buffer)

                override fun read(buffer: ByteArray, offset: Int, count: Int): Int =
                    stream.read(buffer, offset, count)

                override fun skip(amount: Long): Long = stream.skip(amount)

                override fun available(): Long = stream.available().toLong()

                override fun rewind() {
                    try {
                        stream.close()
                        val newStream = BufferedInputStream(FileInputStream(file))
                        // Replace the stream field with the new stream
                        stream.close()
                        val streamField = this.javaClass.getDeclaredField("stream")
                        streamField.isAccessible = true
                        streamField.set(this, newStream)
                    } catch (e: IOException) {
                        // If reset fails, reopen the stream
                        try {
                            val newStream = BufferedInputStream(FileInputStream(file))
                            stream.close()
                            val streamField = this.javaClass.getDeclaredField("stream")
                            streamField.isAccessible = true
                            streamField.set(this, newStream)
                        } catch (e: Exception) {
                            throw IOException("Failed to rewind stream", e)
                        }
                    } catch (e: Exception) {
                        throw IOException("Failed to rewind stream", e)
                    }
                }

                override fun isClosed(): Boolean = closed

                override fun close() {
                    stream.close()
                    closed = true
                }

                override fun canRewind(): Boolean = true

                override fun canRead(): Boolean = true

                override fun canWrite(): Boolean = false

                override fun write(value: Byte) { throw UnsupportedOperationException() }

                override fun write(buffer: ByteArray) { throw UnsupportedOperationException() }

                override fun write(buffer: ByteArray, offset: Int, count: Int) {
                    throw UnsupportedOperationException()
                }
            }
        }
    }

    private lateinit var fileLocator: BackupFileLocator

    @Before
    fun setupFileLocator() {
        fileLocator = Mockito.mock(BackupFileLocator::class.java)
    }

    @Test
    fun `Imported database is taken from zip when available`() {
        // Create a temporary database file
        val db = File.createTempFile("newpipe_", "")
        db.deleteOnExit()
        val dbJournal = File(db.parent, db.name + "-journal")
        val dbShm = File(db.parent, db.name + "-shm")
        val dbWal = File(db.parent, db.name + "-wal")

        // Delete any existing journal files to ensure clean test state
        dbJournal.delete()
        dbShm.delete()
        dbWal.delete()

        // Setup mocks
        `when`(fileLocator.db).thenReturn(db)
        `when`(fileLocator.dbJournal).thenReturn(dbJournal)
        `when`(fileLocator.dbShm).thenReturn(dbShm)
        `when`(fileLocator.dbWal).thenReturn(dbWal)

        // Create a real file with the test data
        val tempZipFile = TestData.createZipFile(includeDb = true)
        val storedFileHelper = TestStoredFileHelper(tempZipFile)

        // Test the extraction
        assertTrue(ImportExportManager(fileLocator).extractDb(storedFileHelper))

        // Verify file size is greater than 0
        assertTrue(Files.size(db.toPath()) > 0)
    }

    @Test
    fun `Database extraction works with database in zip root`() {
        // Create a temporary database file and related files
        val db = File.createTempFile("newpipe_", "")
        db.deleteOnExit()
        val dbJournal = File(db.parent, db.name + "-journal")
        val dbShm = File(db.parent, db.name + "-shm")
        val dbWal = File(db.parent, db.name + "-wal")

        // Delete any existing journal files to ensure clean test state
        dbJournal.delete()
        dbShm.delete()
        dbWal.delete()

        // Setup mocks
        `when`(fileLocator.db).thenReturn(db)
        `when`(fileLocator.dbJournal).thenReturn(dbJournal)
        `when`(fileLocator.dbShm).thenReturn(dbShm)
        `when`(fileLocator.dbWal).thenReturn(dbWal)

        // Create a real file with the test data
        val tempZipFile = TestData.createZipFile(includeDb = true)
        val storedFileHelper = TestStoredFileHelper(tempZipFile)

        // Test the extraction
        assertTrue(ImportExportManager(fileLocator).extractDb(storedFileHelper))
        assertFalse(dbJournal.exists())
        assertFalse(dbWal.exists())
        assertFalse(dbShm.exists())
        assertTrue(Files.size(db.toPath()) > 0)
    }

    @Test
    fun `Database not extracted when not in zip`() {
        // Create a temporary database file and related files
        val db = File.createTempFile("newpipe_", "")
        db.deleteOnExit()
        val dbJournal = File(db.parent, db.name + "-journal")
        val dbShm = File(db.parent, db.name + "-shm")
        val dbWal = File(db.parent, db.name + "-wal")

        // Delete any existing journal files to ensure clean test state
        dbJournal.delete()
        dbShm.delete()
        dbWal.delete()

        // Create the journal files to test they remain when extraction fails
        dbJournal.createNewFile()
        dbShm.createNewFile()
        dbWal.createNewFile()

        // Setup mocks
        `when`(fileLocator.db).thenReturn(db)
        `when`(fileLocator.dbJournal).thenReturn(dbJournal)
        `when`(fileLocator.dbShm).thenReturn(dbShm)
        `when`(fileLocator.dbWal).thenReturn(dbWal)

        // Create a real file with the test data - without DB
        val tempZipFile = TestData.createZipFile(includeDb = false)
        val storedFileHelper = TestStoredFileHelper(tempZipFile)

        // Test the extraction
        assertFalse(ImportExportManager(fileLocator).extractDb(storedFileHelper))
        assertTrue(dbJournal.exists())
        assertTrue(dbShm.exists())
        assertTrue(dbWal.exists())
        assertEquals(0, Files.size(db.toPath()))
    }

    @Test
    fun `Importing preferences from JSON works on valid file`() {
        // Create mockups for preferences
        val preferences = Mockito.mock(SharedPreferences::class.java)
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)

        // Setup the mocks
        `when`(preferences.edit()).thenReturn(editor)
        `when`(editor.clear()).thenReturn(editor)
        `when`(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor)
        `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
        `when`(editor.putInt(anyString(), anyInt())).thenReturn(editor)
        `when`(editor.putLong(anyString(), Mockito.anyLong())).thenReturn(editor)
        `when`(editor.putStringSet(anyString(), any())).thenReturn(editor)
        `when`(editor.commit()).thenReturn(true)

        // Create a real file with the test data
        val tempZipFile = TestData.createZipFile(includeJson = true)
        val storedFileHelper = TestStoredFileHelper(tempZipFile)

        // Test importing preferences
        ImportExportManager(fileLocator).loadJsonPrefs(storedFileHelper, preferences)

        // Verify the expected calls were made
        verify(editor, atLeastOnce()).putString(anyString(), anyString())
        verify(editor, atLeastOnce()).putBoolean(anyString(), anyBoolean())
        verify(editor).commit()
    }

    @Test
    fun `Importing preferences with a serialization injected class should fail`() {
        // Create a real file with the test data
        val tempZipFile = TestData.createZipFile(includeVulnerable = true)
        val storedFileHelper = TestStoredFileHelper(tempZipFile)

        // Create mock for preferences
        val preferences = Mockito.mock(SharedPreferences::class.java)

        // This should throw a ClassNotFoundException because we're trying to deserialize a class
        // that's not in the whitelist in PreferencesObjectInputStream
        val exception = assertThrows(ClassNotFoundException::class.java) {
            ImportExportManager(fileLocator).loadSerializedPrefs(storedFileHelper, preferences)
        }

        // Verify the exception contains information about class not allowed
        assertTrue(
            "Exception message should contain 'Class not allowed': ${exception.message}",
            exception.message?.contains("Class not allowed") == true
        )
    }

    @Test
    fun `Exported preferences contain all the original preferences`() {
        Assume.assumeTrue(
            "Test doesn't work on Windows because of unresolved paths",
            System.getProperty("os.name").lowercase().indexOf("win") < 0
        )

        val exportedZipFile = TestData.createJsonZip()
        // Get the actual File from the StoredFileHelper using reflection
        val field = StoredFileHelper::class.java.getDeclaredField("source")
        field.isAccessible = true
        val source = field.get(exportedZipFile) as String
        val file = File(URI.create(source))

        val prefsFile = ZipFile(file)
        val prefsEntry = prefsFile.getEntry("settings/newpipe.json")
        val prefsJson = prefsFile.getInputStream(prefsEntry).reader().readText()
        // Explicitly cast to Reader to resolve ambiguity
        val jsonPrefs = JsonParser.`object`().from(prefsJson.reader())

        assertEquals("one", jsonPrefs.getString("test_string", ""))
        assertEquals(12345, jsonPrefs.getInt("test_int", 0))
        assertEquals(1.2345, jsonPrefs.getDouble("test_double", 0.0), 0.0)
        assertTrue(jsonPrefs.getBoolean("test_bool", false))

        prefsFile.close()
    }
}
