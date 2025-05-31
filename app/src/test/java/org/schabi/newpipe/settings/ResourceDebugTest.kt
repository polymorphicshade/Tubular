package org.schabi.newpipe.settings

import org.junit.Test
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * A test class specifically for debugging resource loading issues
 */
class ResourceDebugTest {

    private val classLoader = javaClass.classLoader!!

    @Test
    fun `Debug resource loading for test files`() {
        val resourcePath = "settings/db_ser_json.zip"
        println("DEBUG: Test running from directory: ${File(".").absolutePath}")
        println("DEBUG: Looking for resource: $resourcePath")

        // Method 1: Using getResource
        val resourceUrl = classLoader.getResource(resourcePath)
        println("DEBUG: Resource URL: $resourceUrl")

        if (resourceUrl != null) {
            println("DEBUG: Resource URL protocol: ${resourceUrl.protocol}")
            println("DEBUG: Resource URL path: ${resourceUrl.path}")
            println("DEBUG: Resource URL file: ${resourceUrl.file}")

            // Try to decode the URL
            val decodedPath = URLDecoder.decode(resourceUrl.file, StandardCharsets.UTF_8.name())
            println("DEBUG: Decoded path: $decodedPath")

            val file = File(decodedPath)
            println("DEBUG: File absolute path: ${file.absolutePath}")
            println("DEBUG: File exists: ${file.exists()}")
        } else {
            println("DEBUG: Resource not found using getResource")
        }

        // Method 2: Using getResourceAsStream
        val resourceStream = classLoader.getResourceAsStream(resourcePath)
        println("DEBUG: Resource stream: ${resourceStream != null}")

        if (resourceStream != null) {
            // Create a temp file
            val tempFile = File.createTempFile("debug_test_", "_resource")
            tempFile.deleteOnExit()

            // Copy stream to temp file
            tempFile.outputStream().use { output ->
                resourceStream.use { input ->
                    input.copyTo(output)
                }
            }

            println("DEBUG: Temp file created: ${tempFile.absolutePath}")
            println("DEBUG: Temp file size: ${tempFile.length()} bytes")
            println("DEBUG: Temp file exists: ${tempFile.exists()}")
        }

        // Method 3: Try to find the file in the project structure
        val possibleLocations = listOf(
            "app/src/test/resources/$resourcePath",
            "src/test/resources/$resourcePath",
            "../src/test/resources/$resourcePath",
            "../../src/test/resources/$resourcePath",
            "../../../src/test/resources/$resourcePath",
            "test/resources/$resourcePath",
            "resources/$resourcePath",
            resourcePath
        )

        println("DEBUG: Searching for file in possible locations:")
        for (location in possibleLocations) {
            val file = File(location)
            println("DEBUG: Location: $location, exists: ${file.exists()}, absolute path: ${file.absolutePath}")
        }

        // Method 4: Search in the classpath for resource directories
        println("DEBUG: Trying to list directories in classpath to find resources folder")
        val classLoader = javaClass.classLoader

        // Try to see if we can list "settings" directory
        val settingsUrl = classLoader.getResource("settings")
        println("DEBUG: Settings directory URL: $settingsUrl")

        if (settingsUrl != null) {
            try {
                val settingsDir = File(settingsUrl.toURI())
                println("DEBUG: Settings directory exists: ${settingsDir.exists()}")
                println("DEBUG: Settings directory absolute path: ${settingsDir.absolutePath}")
                println("DEBUG: Settings directory contents:")
                settingsDir.listFiles()?.forEach { file ->
                    println("DEBUG: - ${file.name} (${file.length()} bytes)")
                }
            } catch (e: Exception) {
                println("DEBUG: Error accessing settings directory: ${e.message}")
            }
        }
    }
}
