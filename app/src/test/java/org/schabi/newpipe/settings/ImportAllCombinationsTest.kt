package org.schabi.newpipe.settings

import android.content.SharedPreferences
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.junit.MockitoSettings
import org.mockito.quality.Strictness
import org.schabi.newpipe.settings.export.BackupFileLocator
import org.schabi.newpipe.settings.export.ImportExportManager
import org.schabi.newpipe.streams.io.StoredFileHelper
import us.shandian.giga.io.FileStream
import java.io.File
import java.io.IOException
import java.nio.file.Files

@MockitoSettings(strictness = Strictness.LENIENT)
class ImportAllCombinationsTest {

    private enum class Ser(val id: String) {
        YES("ser"),
        VULNERABLE("vulnser"),
        NO("noser");
    }

    private data class FailData(
        val containsDb: Boolean,
        val containsSer: Ser,
        val containsJson: Boolean,
        val filename: String,
        val throwable: Throwable,
    )

    private fun getTestResource(
        containsDb: Boolean,
        containsSer: Ser,
        containsJson: Boolean
    ): File {
        return when {
            containsDb && containsSer == Ser.YES && containsJson -> TestData.createDbSerJsonZip()
            containsDb && containsSer == Ser.YES && !containsJson -> TestData.createDbSerNojsonZip()
            containsDb && containsSer == Ser.VULNERABLE && containsJson -> TestData.createDbVulnserJsonZip()
            containsDb && containsSer == Ser.VULNERABLE && !containsJson -> TestData.createDbVulnserNojsonZip()
            containsDb && containsSer == Ser.NO && containsJson -> TestData.createDbNoserJsonZip()
            containsDb && containsSer == Ser.NO && !containsJson -> TestData.createDbNoserNojsonZip()
            !containsDb && containsSer == Ser.YES && containsJson -> TestData.createNodbSerJsonZip()
            !containsDb && containsSer == Ser.YES && !containsJson -> TestData.createNodbSerNojsonZip()
            !containsDb && containsSer == Ser.VULNERABLE && containsJson -> TestData.createNodbVulnserJsonZip()
            !containsDb && containsSer == Ser.VULNERABLE && !containsJson -> TestData.createNodbVulnserNojsonZip()
            !containsDb && containsSer == Ser.NO && containsJson -> TestData.createNodbNoserJsonZip()
            !containsDb && containsSer == Ser.NO && !containsJson -> TestData.createNodbNoserNojsonZip()
            else -> throw IllegalArgumentException("Invalid test case combination")
        }
    }

    private fun testZipCombination(
        containsDb: Boolean,
        containsSer: Ser,
        containsJson: Boolean,
        filename: String,
        runTest: (test: () -> Unit) -> Unit,
    ) {
        val zipFile = getTestResource(containsDb, containsSer, containsJson)
        val zip = Mockito.mock(StoredFileHelper::class.java)
        Mockito.`when`(zip.stream).thenReturn(FileStream(zipFile))

        val fileLocator = Mockito.mock(BackupFileLocator::class.java)
        val db = File.createTempFile("newpipe_", "")
        db.deleteOnExit()
        val dbJournal = File.createTempFile("newpipe_", "")
        dbJournal.deleteOnExit()
        val dbWal = File.createTempFile("newpipe_", "")
        dbWal.deleteOnExit()
        val dbShm = File.createTempFile("newpipe_", "")
        dbShm.deleteOnExit()

        Mockito.`when`(fileLocator.db).thenReturn(db)
        Mockito.`when`(fileLocator.dbJournal).thenReturn(dbJournal)
        Mockito.`when`(fileLocator.dbShm).thenReturn(dbShm)
        Mockito.`when`(fileLocator.dbWal).thenReturn(dbWal)

        if (containsDb) {
            runTest {
                Assert.assertTrue(ImportExportManager(fileLocator).extractDb(zip))
                Assert.assertFalse(dbJournal.exists())
                Assert.assertFalse(dbWal.exists())
                Assert.assertFalse(dbShm.exists())
                Assert.assertTrue("database file size is zero", Files.size(db.toPath()) > 0)
            }
        } else {
            runTest {
                Assert.assertFalse(ImportExportManager(fileLocator).extractDb(zip))
                Assert.assertTrue(dbJournal.exists())
                Assert.assertTrue(dbWal.exists())
                Assert.assertTrue(dbShm.exists())
                Assert.assertEquals(0, Files.size(db.toPath()))
            }
        }

        val preferences = Mockito.mock(SharedPreferences::class.java)
        var editor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(preferences.edit()).thenReturn(editor)
        Mockito.`when`(editor.commit()).thenReturn(true)
        Mockito.`when`(editor.clear()).thenReturn(editor)

        // Fix UnfinishedStubbingException by stubbing all possible methods called on editor
        Mockito.`when`(editor.putBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(editor)
        Mockito.`when`(editor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(editor)
        Mockito.`when`(editor.putInt(Mockito.anyString(), Mockito.anyInt())).thenReturn(editor)
        Mockito.`when`(editor.putLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(editor)
        Mockito.`when`(editor.putFloat(Mockito.anyString(), Mockito.anyFloat())).thenReturn(editor)
        Mockito.`when`(editor.putStringSet(Mockito.anyString(), any())).thenReturn(editor)

        when (containsSer) {
            Ser.YES -> runTest {
                Assert.assertTrue(ImportExportManager(fileLocator).exportHasSerializedPrefs(zip))
                ImportExportManager(fileLocator).loadSerializedPrefs(zip, preferences)

                Mockito.verify(editor, Mockito.times(1)).clear()
                Mockito.verify(editor, Mockito.times(1)).commit()
                Mockito.verify(editor, Mockito.atLeastOnce())
                    .putBoolean(Mockito.anyString(), Mockito.anyBoolean())
                Mockito.verify(editor, Mockito.atLeastOnce())
                    .putString(Mockito.anyString(), Mockito.anyString())
                Mockito.verify(editor, Mockito.atLeastOnce())
                    .putInt(Mockito.anyString(), Mockito.anyInt())
            }
            Ser.VULNERABLE -> runTest {
                Assert.assertTrue(ImportExportManager(fileLocator).exportHasSerializedPrefs(zip))

                // For vulnerable serialization, we expect ClassNotFoundException with a message containing "Class not allowed"
                val exception = Assert.assertThrows(ClassNotFoundException::class.java) {
                    ImportExportManager(fileLocator).loadSerializedPrefs(zip, preferences)
                }

                Assert.assertTrue(
                    "Exception message should contain 'Class not allowed': ${exception.message}",
                    exception.message?.contains("Class not allowed") == true
                )

                Mockito.verify(editor, Mockito.never()).clear()
                Mockito.verify(editor, Mockito.never()).commit()
            }
            Ser.NO -> runTest {
                Assert.assertFalse(ImportExportManager(fileLocator).exportHasSerializedPrefs(zip))
                Assert.assertThrows(IOException::class.java) {
                    ImportExportManager(fileLocator).loadSerializedPrefs(zip, preferences)
                }

                Mockito.verify(editor, Mockito.never()).clear()
                Mockito.verify(editor, Mockito.never()).commit()
            }
        }

        // recreate editor mock so verify() behaves correctly
        editor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(preferences.edit()).thenReturn(editor)
        Mockito.`when`(editor.commit()).thenReturn(true)
        Mockito.`when`(editor.clear()).thenReturn(editor)

        // Fix UnfinishedStubbingException by stubbing all possible methods called on editor
        Mockito.`when`(editor.putBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(editor)
        Mockito.`when`(editor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(editor)
        Mockito.`when`(editor.putInt(Mockito.anyString(), Mockito.anyInt())).thenReturn(editor)
        Mockito.`when`(editor.putLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(editor)
        Mockito.`when`(editor.putFloat(Mockito.anyString(), Mockito.anyFloat())).thenReturn(editor)
        Mockito.`when`(editor.putStringSet(Mockito.anyString(), any())).thenReturn(editor)

        if (containsJson) {
            runTest {
                Assert.assertTrue(ImportExportManager(fileLocator).exportHasJsonPrefs(zip))
                ImportExportManager(fileLocator).loadJsonPrefs(zip, preferences)

                Mockito.verify(editor, Mockito.times(1)).clear()
                Mockito.verify(editor, Mockito.times(1)).commit()
                Mockito.verify(editor, Mockito.atLeastOnce())
                    .putBoolean(Mockito.anyString(), Mockito.anyBoolean())
                Mockito.verify(editor, Mockito.atLeastOnce())
                    .putString(Mockito.anyString(), Mockito.anyString())
                Mockito.verify(editor, Mockito.atLeastOnce())
                    .putInt(Mockito.anyString(), Mockito.anyInt())
            }
        } else {
            runTest {
                Assert.assertFalse(ImportExportManager(fileLocator).exportHasJsonPrefs(zip))
                Assert.assertThrows(IOException::class.java) {
                    ImportExportManager(fileLocator).loadJsonPrefs(zip, preferences)
                }

                Mockito.verify(editor, Mockito.never()).clear()
                Mockito.verify(editor, Mockito.never()).commit()
            }
        }
    }

    @Test
    fun `Importing all possible combinations of zip files`() {
        val failedAssertions = mutableListOf<FailData>()
        for (containsDb in listOf(true, false)) {
            for (containsSer in Ser.entries) {
                for (containsJson in listOf(true, false)) {
                    val filename = "settings/${if (containsDb) "db" else "nodb"}_${
                    containsSer.id}_${if (containsJson) "json" else "nojson"}.zip"
                    testZipCombination(containsDb, containsSer, containsJson, filename) { test ->
                        try {
                            test()
                        } catch (e: Throwable) {
                            failedAssertions.add(
                                FailData(
                                    containsDb, containsSer, containsJson,
                                    filename, e
                                )
                            )
                        }
                    }
                }
            }
        }

        if (failedAssertions.isNotEmpty()) {
            for (a in failedAssertions) {
                println(
                    "Assertion failed with containsDb=${a.containsDb}, containsSer=${
                    a.containsSer}, containsJson=${a.containsJson}, filename=${a.filename}:"
                )
                a.throwable.printStackTrace()
                println()
            }
            Assert.fail("${failedAssertions.size} assertions failed")
        }
    }
}
