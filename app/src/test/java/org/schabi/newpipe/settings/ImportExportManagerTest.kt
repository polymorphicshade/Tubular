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
import org.mockito.junit.MockitoSettings
import org.mockito.quality.Strictness
import org.schabi.newpipe.settings.export.BackupFileLocator
import org.schabi.newpipe.settings.export.ImportExportManager
import org.schabi.newpipe.streams.io.StoredFileHelper
import us.shandian.giga.io.FileStream
import java.io.File
import java.io.ObjectInputStream
import java.nio.file.Files
import java.util.zip.ZipFile

@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner::class)
class ImportExportManagerTest {

    private lateinit var fileLocator: BackupFileLocator
    private lateinit var storedFileHelper: StoredFileHelper

    @Before
    fun setupFileLocator() {
        fileLocator = Mockito.mock(BackupFileLocator::class.java)
        storedFileHelper = Mockito.mock(StoredFileHelper::class.java)
    }

    @Test
    fun `The settings must be exported successfully in the correct format`() {
        val db = TestData.createDbFile()
        `when`(fileLocator.db).thenReturn(db)

        val expectedPreferences = mapOf("such pref" to "much wow")
        val sharedPreferences =
            Mockito.mock(SharedPreferences::class.java)
        `when`(sharedPreferences.all).thenReturn(expectedPreferences)

        val output = File.createTempFile("newpipe_", "")
        output.deleteOnExit()

        `when`(storedFileHelper.openAndTruncateStream()).thenReturn(FileStream(output))
        ImportExportManager(fileLocator).exportDatabase(sharedPreferences, storedFileHelper)

        val zipFile = ZipFile(output)
        val entries = zipFile.entries().toList()
        assertEquals(3, entries.size)

        zipFile.getInputStream(entries.first { it.name == "newpipe.db" }).use { actual ->
            db.inputStream().use { expected ->
                assertEquals(expected.reader().readText(), actual.reader().readText())
            }
        }

        zipFile.getInputStream(entries.first { it.name == "newpipe.settings" }).use { actual ->
            val actualPreferences = ObjectInputStream(actual).readObject()
            assertEquals(expectedPreferences, actualPreferences)
        }

        zipFile.getInputStream(entries.first { it.name == "preferences.json" }).use { actual ->
            val actualPreferences = JsonParser.`object`().from(actual)
            assertEquals(expectedPreferences, actualPreferences)
        }
    }

    @Test
    fun `Ensuring db directory existence must work`() {
        val dir = Files.createTempDirectory("newpipe_").toFile()
        dir.deleteOnExit()
        Assume.assumeTrue(dir.delete())
        `when`(fileLocator.dbDir).thenReturn(dir)

        ImportExportManager(fileLocator).ensureDbDirectoryExists()
        assertTrue(dir.exists())
    }

    @Test
    fun `Ensuring db directory existence must work when the directory already exists`() {
        val dir = Files.createTempDirectory("newpipe_").toFile()
        dir.deleteOnExit()
        `when`(fileLocator.dbDir).thenReturn(dir)

        ImportExportManager(fileLocator).ensureDbDirectoryExists()
        assertTrue(dir.exists())
    }

    @Test
    fun `The database must be extracted from the zip file`() {
        val db = File.createTempFile("newpipe_", "")
        db.deleteOnExit()
        val dbJournal = File.createTempFile("newpipe_", "")
        dbJournal.deleteOnExit()
        val dbWal = File.createTempFile("newpipe_", "")
        dbWal.deleteOnExit()
        val dbShm = File.createTempFile("newpipe_", "")
        dbShm.deleteOnExit()

        `when`(fileLocator.db).thenReturn(db)
        `when`(fileLocator.dbJournal).thenReturn(dbJournal)
        `when`(fileLocator.dbShm).thenReturn(dbShm)
        `when`(fileLocator.dbWal).thenReturn(dbWal)

        val zipFile = TestData.createDbSerJsonZip()
        `when`(storedFileHelper.stream).thenReturn(FileStream(zipFile))

        val success = ImportExportManager(fileLocator).extractDb(storedFileHelper)

        assertTrue(success)
        assertFalse(dbJournal.exists())
        assertFalse(dbWal.exists())
        assertFalse(dbShm.exists())
        assertTrue("database file size is zero", Files.size(db.toPath()) > 0)
    }

    @Test
    fun `Extracting the database from an empty zip must not work`() {
        val db = File.createTempFile("newpipe_", "")
        db.deleteOnExit()
        val dbJournal = File.createTempFile("newpipe_", "")
        dbJournal.deleteOnExit()
        val dbWal = File.createTempFile("newpipe_", "")
        dbWal.deleteOnExit()
        val dbShm = File.createTempFile("newpipe_", "")
        dbShm.deleteOnExit()

        `when`(fileLocator.db).thenReturn(db)
        `when`(fileLocator.dbJournal).thenReturn(dbJournal)
        `when`(fileLocator.dbShm).thenReturn(dbShm)
        `when`(fileLocator.dbWal).thenReturn(dbWal)

        val emptyZip = TestData.createNodbNoserNojsonZip()
        `when`(storedFileHelper.stream).thenReturn(FileStream(emptyZip))

        val success = ImportExportManager(fileLocator).extractDb(storedFileHelper)

        assertFalse(success)
        assertTrue(dbJournal.exists())
        assertTrue(dbWal.exists())
        assertTrue(dbShm.exists())
        assertEquals(0, Files.size(db.toPath()))
    }

    @Test
    fun `Contains setting must return true if a settings file exists in the zip`() {
        val zipFile = TestData.createDbSerJsonZip()
        `when`(storedFileHelper.stream).thenReturn(FileStream(zipFile))

        assertTrue(ImportExportManager(fileLocator).exportHasSerializedPrefs(storedFileHelper))
    }

    @Test
    fun `Contains setting must return false if no settings file exists in the zip`() {
        val emptyZip = TestData.createNodbNoserNojsonZip()
        `when`(storedFileHelper.stream).thenReturn(FileStream(emptyZip))

        assertFalse(ImportExportManager(fileLocator).exportHasSerializedPrefs(storedFileHelper))
    }

    @Test
    fun `Preferences must be set from the settings file`() {
        val zipFile = TestData.createDbSerJsonZip()
        `when`(storedFileHelper.stream).thenReturn(FileStream(zipFile))

        val preferences = Mockito.mock(SharedPreferences::class.java)
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)
        `when`(preferences.edit()).thenReturn(editor)
        `when`(editor.commit()).thenReturn(true)
        `when`(editor.clear()).thenReturn(editor)

        // Fix UnfinishedStubbingException by stubbing all possible methods called on editor
        `when`(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor)
        `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
        `when`(editor.putInt(anyString(), anyInt())).thenReturn(editor)
        `when`(editor.putLong(anyString(), Mockito.anyLong())).thenReturn(editor)
        `when`(editor.putFloat(anyString(), Mockito.anyFloat())).thenReturn(editor)
        `when`(editor.putStringSet(anyString(), any())).thenReturn(editor)

        ImportExportManager(fileLocator).loadSerializedPrefs(storedFileHelper, preferences)

        // Verify that editor methods were called
        verify(editor, atLeastOnce()).putBoolean(anyString(), anyBoolean())
        verify(editor, atLeastOnce()).putString(anyString(), anyString())
        verify(editor, atLeastOnce()).putInt(anyString(), anyInt())
    }

    @Test
    fun `Importing preferences with a serialization injected class should fail`() {
        val vulnZip = TestData.createDbVulnserJsonZip()
        `when`(storedFileHelper.stream).thenReturn(FileStream(vulnZip))

        val preferences = Mockito.mock(SharedPreferences::class.java)

        // This should throw a ClassNotFoundException because we're trying to deserialize a class
        // that's not in the whitelist in PreferencesObjectInputStream
        val exception = assertThrows(ClassNotFoundException::class.java) {
            ImportExportManager(fileLocator).loadSerializedPrefs(storedFileHelper, preferences)
        }

        // Verify the exception contains information about class not allowed
        assertTrue(
            "Exception should indicate class not allowed: ${exception.message}",
            exception.message?.contains("Class not allowed") == true
        )
    }
}
