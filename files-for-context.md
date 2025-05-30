# Files and Outputs to Provide

## AndroidManifest.xml
- **Why:** This file defines the app's package name (org.schabi.newpipe), the main activity (.MainActivity), and other components critical to resolving the launch error (Activity class does not exist).
- **What to Include:** The full file you provided earlier, which confirms the package and activity structure.
- **Location:** app/src/main/AndroidManifest.xml in F:\Program Files\Tubular.

## build.gradle Files
- **Why:** These files contain build configurations (e.g., app/build.gradle with debuggable settings and deprecation issues like archivesBaseName). They're essential for fixing build warnings and disabling debugging.
- **What to Include:**
  - app/build.gradle (especially the android and buildTypes blocks).
  - build.gradle (project-level) if modified.
- **Location:** F:\Program Files\Tubular\app\build.gradle and F:\Program Files\Tubular\build.gradle.

## launch.json
- **Why:** This file configures how Cursor launches the app, including the package name and debug settings. It needs updating to org.schabi.newpipe and noDebug: true.
- **What to Include:** The current or updated version (as suggested earlier).
- **Location:** F:\Program Files\Tubular\.vscode\launch.json (create if missing).

## Test Files and Resources
- **Why:** The unit test failures (ImportExportManagerTest.kt, ImportAllCombinationsTest.kt) were due to FileNotFoundException and UnfinishedStubbingException. The resources folder contains test zip files (e.g., db_ser_json.zip) critical for fixing tests.
- **What to Include:**
  - ImportExportManagerTest.kt and ImportAllCombinationsTest.kt from app/src/test/java/org/schabi/newpipe/settings/.
  - The resources folder (e.g., settings/db_ser_json.zip, newpipe.db) and its current location.
- **Location:** F:\Program Files\Tubular\app\src\test\java\org\schabi\newpipe\settings\ and the resources folder (move to src/test/resources/).

## Build Output Logs
- **Why:** The initial build output (.\gradlew build --warning-mode all) detailed test failures, deprecations, and warnings. The successful .\gradlew build -x test output shows the current build state.
- **What to Include:**
  - The full output from .\gradlew build --warning-mode all (with test failures).
  - The output from .\gradlew build -x test (successful build).
- **How to Provide:** Copy and paste the terminal outputs or save as text files.

## ADB and Launch Outputs
- **Why:** These show the progression of ADB issues (e.g., ECONNREFUSED, Activity class does not exist) and the current state of device interaction.
- **What to Include:**
  - The debug console output (Checking build... Launching on device...) with "waiting for debugger."
  - The manual launch output (adb install success, adb shell am start error).
  - adb devices output (showing 0I73C18I24101774 device).
- **How to Provide:** Copy from Cursor's debug console or terminal.

## logcat.txt (if Captured)
- **Why:** Logs will reveal runtime errors or confirm app behavior once launched. The USB disconnect issue prevented capture, but any partial logs are useful.
- **What to Include:** The file from adb logcat > logcat.txt if you manage to stabilize the connection.
- **How to Provide:** Share the file or its contents after running the command.

## tasks.json (Optional)
- **Why:** This tracks development tasks (e.g., ReturnYouTubeDislike toggle), aligning with your structured workflow.
- **What to Include:** The suggested version or your current file.
- **Location:** F:\Program Files\Tubular\.vscode\tasks.json (create if missing).