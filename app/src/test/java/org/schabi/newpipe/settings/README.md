# Test Data Generation Utility

## Overview

This directory contains a `TestData` utility class that generates test data programmatically instead of relying on physical resource files. This approach solves cross-platform path handling issues that were causing tests to fail, particularly on Windows systems.

## Key Features

- **Platform Independence**: Generates all test data in memory, eliminating path-related issues across different operating systems
- **Realistic Test Data**: Creates realistic binary content for databases, preferences, and serialized data
- **Security Testing**: Properly simulates vulnerable serialized data to test PreferencesObjectInputStream's security features
- **Consistent Test Environment**: Ensures tests run identically across all development environments

## How It Works

The `TestData` utility provides methods to create:

1. **Database Files**: Simulates SQLite database files with realistic headers and content
2. **ZIP Archives**: Creates various combinations of ZIP files containing:
   - Database files (`newpipe.db`)
   - Serialized preferences (`newpipe.settings`) 
   - JSON preferences (`preferences.json`)
3. **Vulnerable Data**: Simulates serialization vulnerability attacks by including non-whitelisted classes that should be rejected by PreferencesObjectInputStream

## Usage in Tests

The utility provides helper methods for creating different test file combinations:

```kotlin
// Create a ZIP with database, serialized preferences, and JSON preferences
val zipFile = TestData.createDbSerJsonZip()

// Create a ZIP with database and vulnerable serialized data (for security testing)
val vulnZip = TestData.createDbVulnserJsonZip()

// Create a database file
val dbFile = TestData.createDbFile()
```

## Security Testing

The utility includes a mechanism to test PreferencesObjectInputStream's security features by:

1. Creating a non-whitelisted class (`MaliciousData`) that should be rejected
2. Embedding this class within a HashMap (which is whitelisted)
3. Serializing the data in a way that triggers the security check during deserialization
4. Verifying that PreferencesObjectInputStream correctly throws a ClassNotFoundException with "Class not allowed" message

## Benefits

- **Reproducibility**: Tests behave consistently regardless of file system or environment
- **Simplicity**: No need to manage physical test resource files
- **Maintainability**: Test data generation is centralized in one utility class
- **Security**: Proper testing of serialization vulnerability protection

## Available Test Files

The utility can generate the following types of test files:

1. **Database Files**: Simulated SQLite database files with realistic headers
2. **ZIP Archives**: Various combinations of:
   - Database files
   - Serialized preferences
   - JSON preferences
   - Malicious serialized content (for security testing)

## Helper Methods

The utility provides 12 different helper methods to create ZIP files with various combinations:

- `createDbSerJsonZip()`: DB + Serialized Prefs + JSON Prefs
- `createDbSerNojsonZip()`: DB + Serialized Prefs
- `createDbVulnserJsonZip()`: DB + Vulnerable Serialized Prefs + JSON Prefs
- `createDbVulnserNojsonZip()`: DB + Vulnerable Serialized Prefs
- `createDbNoserJsonZip()`: DB + JSON Prefs
- `createDbNoserNojsonZip()`: DB only
- `createNodbSerJsonZip()`: Serialized Prefs + JSON Prefs
- `createNodbSerNojsonZip()`: Serialized Prefs only
- `createNodbVulnserJsonZip()`: Vulnerable Serialized Prefs + JSON Prefs
- `createNodbVulnserNojsonZip()`: Vulnerable Serialized Prefs only
- `createNodbNoserJsonZip()`: JSON Prefs only
- `createNodbNoserNojsonZip()`: Empty ZIP (no content)

## Implementation Details

- The database content includes a realistic SQLite header followed by test data
- Preferences data matches the actual app settings format
- All files are created as temporary files with `deleteOnExit()` to prevent test file accumulation
- Vulnerable serialized data is created to test security handling of malicious input

## Why This Approach?

This approach was implemented to solve platform-specific path handling issues that were causing test failures, particularly on Windows systems. By generating all test data programmatically at runtime, we eliminate any dependency on physical resource files and ensure tests run consistently across all platforms. 