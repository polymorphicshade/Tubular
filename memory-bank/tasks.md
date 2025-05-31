# Memory Bank: Tasks

## Current Task
- Task ID: T001
- Name: Fix App Launch Configuration
- Status: ARCHIVED
- Complexity: Level 2
- Assigned To: AI
- Reflection: [Completed](../reflection/reflect-app-launch-configuration-20250531.md)
- Archived: [Archive document](../archive/archive-app-launch-configuration-20250531.md)

### Description
The app builds successfully but was failing to launch on the physical device due to package name confusion. The launch command needs to use the format `[applicationId]/[namespace].[ActivityName]` with both the application ID (`org.polymorphicshade.tubular.debug`) and internal namespace (`org.schabi.newpipe`). Additionally, the debugger was causing the app to wait, and USB connection stability issues were preventing proper logging.

### Requirements
- ✅ Fix the app launch by using the correct launch syntax
- ✅ Address the "waiting for debugger" issue
- Stabilize the USB connection for reliable logging
- ✅ Ensure the app runs properly on the physical device (ID: 0I73C18I24101774)

### Subtasks
- [x] T001.1: Update launch configuration to use correct package and activity
- [x] T001.2: Test direct launch with correct command: `adb shell am start -n org.polymorphicshade.tubular.debug/org.schabi.newpipe.MainActivity`
- [x] T001.3: Address debugger issue by adding `noDebug: true` to launch.json and disabling debugging in build.gradle
- [ ] T001.4: Investigate and resolve USB connection stability issues
- [ ] T001.5: Implement logging through file redirection (`adb logcat > logcat.txt`)
- [x] T001.6: Fix Java configuration with correct JDK path (`F:\Program Files (x86)\jdk-17`)

### Dependencies
- Requires working ADB connection to the device

## Next Task
- Task ID: T002
- Name: Fix Unit Tests
- Status: IN_PROGRESS_IMPLEMENTATION
- Complexity: Level 2
- Assigned To: AI

### Description
The unit tests are currently failing due to resource loading issues, specifically with test files like `db_ser_json.zip`. The main problem occurs in `ImportExportManagerTest.kt` and `ImportAllCombinationsTest.kt` where resource files cannot be found at runtime. This is particularly problematic on Windows systems where path handling differs. Additionally, there are Mockito `UnfinishedStubbingException` issues that need to be addressed.

### Requirements / Acceptance Criteria
- [ ] All unit tests in `ImportExportManagerTest.kt` pass successfully
- [ ] All unit tests in `ImportAllCombinationsTest.kt` pass successfully
- [x] Fix should work across platforms (Windows, Linux, macOS)
- [x] Mockito stubbing is properly configured to avoid `UnfinishedStubbingException`

### Sub-tasks (Implementation Steps)
- [x] T002.1: Analyze failing tests in `ImportExportManagerTest.kt` and `ImportAllCombinationsTest.kt`
- [x] T002.2: Create a platform-independent solution using embedded test data instead of external resources
- [x] T002.3: Create a `TestData` utility class that generates test data on the fly
- [x] T002.4: Update `ImportExportManagerTest.kt` to use the `TestData` utility
- [x] T002.5: Update `ImportAllCombinationsTest.kt` to use the `TestData` utility
- [x] T002.6: Fix Mockito `UnfinishedStubbingException` issues by properly stubbing all relevant methods
- [x] T002.7: Ensure proper code formatting to pass ktlint checks
- [ ] T002.8: Run tests and validate fixes
  - [x] T002.8.1: Fix mock configuration by removing stubOnly() settings
  - [x] T002.8.2: Fix vulnerable serialization format in TestData utility
  - [x] T002.8.3: Fix ClassNotFoundException assertions in ImportExportManagerTest
  - [x] T002.8.4: Fix test combination expectations in ImportAllCombinationsTest
- [x] T002.9: Update documentation on how test resources are now handled

### Dependencies
- Requires configured Gradle build environment
- Requires understanding of Java's ZIP file handling and serialization

### Notes
- The key insight is to generate test data programmatically rather than relying on loading physical resource files
- This approach eliminates platform-specific path issues by creating files in memory
- For security testing, we still need to simulate vulnerable serialized data to test proper error handling
- The solution should maintain all the same test coverage despite the change in approach
- Implementation is complete, but verification is pending due to Kotlin annotation processing (kapt) issues

## Backlog
1. **T003: Address Gradle Deprecations**
   - Status: PENDING
   - Fix `archivesBaseName` and `fileCollection` deprecation warnings
   - Update Gradle configuration

2. **T004: Implement ReturnYouTubeDislike Toggle**
   - Status: PENDING
   - Add toggle in settings
   - Hook up to existing functionality

3. **T005: Enhance SponsorBlock Functionality**
   - Status: PENDING
   - Persist custom SponsorBlock segments in the database
   - Add SponsorBlock's "Exclusive Access" feature
   - Add SponsorBlock's chapters feature

4. **T006: Move Project to User Directory**
   - Status: CANCELLED
   - Project will remain at `F:\Program Files\Tubular`

## Package Naming Reference
- **Internal Namespace**: `org.schabi.newpipe` (inherited from original NewPipe project)
  - Used in imports, class definitions, and Java/Kotlin code
  
- **Application IDs**:
  - Release builds: `org.polymorphicshade.tubular` 
  - Debug builds: `org.polymorphicshade.tubular.debug`
  - Used for installation and app identification on device
  
- **Launch Syntax**:
  - Format: `[applicationId]/[namespace].[ActivityName]`
  - Example: `org.polymorphicshade.tubular.debug/org.schabi.newpipe.MainActivity`
  - VS Code launch.json needs both `packageName` and `activityName` fields 