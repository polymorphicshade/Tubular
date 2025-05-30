# Memory Bank: Tasks

## Current Task
- Task ID: T001
- Name: Fix App Launch Configuration
- Status: PENDING
- Complexity: Level 2
- Assigned To: AI

### Description
The app builds successfully but fails to launch on the physical device due to a package name mismatch. The launch command is currently using `org.polymorphicshade.tubular.debug` instead of the correct package `org.schabi.newpipe`. Additionally, the debugger is causing the app to wait, and USB connection stability issues are preventing proper logging.

### Requirements
- Fix the app launch by using the correct package name
- Address the "waiting for debugger" issue
- Stabilize the USB connection for reliable logging
- Ensure the app runs properly on the physical device (ID: 0I73C18I24101774)

### Subtasks
- [ ] T001.1: Update launch configuration to use `org.schabi.newpipe` package name
- [ ] T001.2: Test direct launch with `adb shell am start -n org.schabi.newpipe/.MainActivity`
- [ ] T001.3: Address debugger issue by adding `noDebug: true` to launch.json or disabling debugging in build.gradle
- [ ] T001.4: Investigate and resolve USB connection stability issues
- [ ] T001.5: Implement logging through file redirection (`adb logcat > logcat.txt`)

### Dependencies
- Requires working ADB connection to the device
- May need permissions to be fixed by moving the project to user directory

## Backlog
1. **T002: Fix Unit Tests**
   - Status: PENDING
   - Move test resources to correct location `src/test/resources/`
   - Resolve Mockito `UnfinishedStubbingException` issues
   - Fix `FileNotFoundException` for `db_ser_json.zip` and other resources

2. **T003: Address Gradle Deprecations**
   - Status: PENDING
   - Fix `archivesBaseName` and `fileCollection` deprecation warnings
   - Update Gradle configuration

3. **T004: Implement ReturnYouTubeDislike Toggle**
   - Status: PENDING
   - Add toggle in settings
   - Hook up to existing functionality

4. **T005: Enhance SponsorBlock Functionality**
   - Status: PENDING
   - Persist custom SponsorBlock segments in the database
   - Add SponsorBlock's "Exclusive Access" feature
   - Add SponsorBlock's chapters feature

5. **T006: Move Project to User Directory**
   - Status: PENDING
   - Relocate project from `F:\Program Files\Tubular` to `C:\Users\Administrator\Tubular`
   - Update paths and configuration files
   - Re-test build and deployment 