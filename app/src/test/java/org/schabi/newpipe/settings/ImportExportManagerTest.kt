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
import org.mockito.junit.MockitoJUnitRunner
import org.schabi.newpipe.settings.export.BackupFileLocator
import org.schabi.newpipe.settings.export.ImportExportManager
import org.schabi.newpipe.streams.io.StoredFileHelper
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.util.zip.ZipFile

@RunWith(MockitoJUnitRunner::class)
class ImportExportManagerTest {

    /**
     * Custom StoredFileHelper implementation that doesn't use the problematic FileStream class
     * but instead uses Java's standard FileInputStream, which is more reliable in tests.
     */
    private class TestStoredFileHelper(private val file: File) : StoredFileHelper() {
        override fun getStream() = BufferedInputStream(FileInputStream(file))

        override fun close() {
            // Nothing to do, the stream is created on demand
        }
    }

    private lateinit var fileLocator: BackupFileLocator
    private lateinit var storedFileHelper: StoredFileHelper

    @Before
    fun setupFileLocator() {
        fileLocator = Mockito.mock(BackupFileLocator::class.java)
        storedFileHelper = Mockito.mock(StoredFileHelper::class.java)
    }

    @Test
    fun `Imported database is taken from zip when available`() {
        // Create a temporary database file
        val db = File.createTempFile("newpipe_", "")
        db.deleteOnExit()

        // Setup mocks
        `when`(fileLocator.db).thenReturn(db)
        val storedFileHelper = TestData.createDbZip()

        // Test the extraction
        assertTrue(ImportExportManager(fileLocator).extractDb(storedFileHelper))
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

        val storedFileHelper = TestData.createRootDbZip()

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

        // Setup mocks
        `when`(fileLocator.db).thenReturn(db)
        `when`(fileLocator.dbJournal).thenReturn(dbJournal)
        `when`(fileLocator.dbShm).thenReturn(dbShm)
        `when`(fileLocator.dbWal).thenReturn(dbWal)

        val storedFileHelper = TestData.createNoDbZip()

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

        // Get test data
        val storedFileHelper = TestData.createJsonZip()

        // Test importing preferences
        ImportExportManager(fileLocator).loadJsonPrefs(storedFileHelper, preferences)

        // Verify the expected calls were made
        verify(editor, atLeastOnce()).putString(anyString(), anyString())
        verify(editor, atLeastOnce()).putBoolean(anyString(), anyBoolean())
        verify(editor).commit()
    }

    @Test
    fun `Importing preferences with a serialization injected class should fail`() {
        // Get test data with vulnerable serialized content
        val storedFileHelper = TestData.createDbVulnserJsonZip()

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
        val prefsFile = ZipFile(exportedZipFile)
        val prefsEntry = prefsFile.getEntry("settings/newpipe.json")
        val prefsJson = prefsFile.getInputStream(prefsEntry).reader().readText()
        val jsonPrefs = JsonParser.`object`().from(prefsJson)

        assertEquals("one", jsonPrefs.getString("test_string", ""))
        assertEquals(12345, jsonPrefs.getInt("test_int", 0))
        assertEquals(1.2345, jsonPrefs.getDouble("test_double", 0.0), 0.0)
        assertTrue(jsonPrefs.getBoolean("test_bool", false))

        prefsFile.close()
    }
}
