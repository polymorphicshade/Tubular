# Memory Bank: Tasks

## Current Task
- Task ID: T001
- Name: Fix App Launch Configuration
- Status: IN_PROGRESS
- Complexity: Level 2
- Assigned To: AI

### Description
The app builds successfully but fails to launch on the physical device due to a package name confusion. The launch command needs to use the internal namespace `org.schabi.newpipe` rather than the application ID `org.polymorphicshade.tubular.debug`. Additionally, the debugger is causing the app to wait, and USB connection stability issues are preventing proper logging.

### Requirements
- Fix the app launch by using the correct internal namespace `org.schabi.newpipe`
- Address the "waiting for debugger" issue
- Stabilize the USB connection for reliable logging
- Ensure the app runs properly on the physical device (ID: 0I73C18I24101774)

### Subtasks
- [x] T001.1: Update launch configuration to use `org.schabi.newpipe` namespace
- [ ] T001.2: Test direct launch with `adb shell am start -n org.schabi.newpipe/.MainActivity`
- [x] T001.3: Address debugger issue by adding `noDebug: true` to launch.json and disabling debugging in build.gradle
- [ ] T001.4: Investigate and resolve USB connection stability issues
- [ ] T001.5: Implement logging through file redirection (`adb logcat > logcat.txt`)
- [x] T001.6: Fix Java configuration with correct JDK path (`F:\Program Files (x86)\jdk-17`)

### Dependencies
- Requires working ADB connection to the device

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
   - Status: CANCELLED
   - Project will remain at `F:\Program Files\Tubular`

## Package Naming Reference
- **Internal Namespace**: `org.schabi.newpipe` (inherited from original NewPipe project)
  - Used in imports, class definitions, and launch commands
  - Example launch command: `adb shell am start -n org.schabi.newpipe/.MainActivity`

- **Application IDs**:
  - Release builds: `org.polymorphicshade.tubular` 
  - Debug builds: `org.polymorphicshade.tubular.debug`
  - Used for installation and app identification on device 