<h1 align="center"><b>Tubular</b></h2>
<h4 align="center">A fork of <a href="https://newpipe.net/">NewPipe</a> (<a href="https://github.com/TeamNewPipe/NewPipe/">Github</a>) that implements <a href="https://sponsor.ajay.app/">SponsorBlock</a> (<a href="https://github.com/ajayyy/SponsorBlock/">Github</a>) and <a href="https://www.returnyoutubedislike.com/">ReturnYouTubeDislike</a> (<a href="https://github.com/Anarios/return-youtube-dislike/">Github</a>).</h4>
<p align="center">Download the APK <a href="https://github.com/polymorphicshade/Tubular/releases/latest">here</a> or get it on F-Droid <a href="https://f-droid.org/packages/org.polymorphicshade.tubular/">here</a>.</p>
<p align="center"><img src="doc/gif/preview_01.gif" width="400"></p>

## Table of Contents
- [APK Info](#apk-info)
- [Features](#features)
- [Development Setup](#development-setup)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
  - [Build & Run](#build--run)
- [Package Structure](#package-structure)
- [Troubleshooting](#troubleshooting)
- [To Do](#to-do)
- [Contributing](#contributing)
- [License](#license)

## APK Info

This is the SHA fingerprint of Tubular's signing key to verify downloaded APKs which are signed by us
```
8A:D7:02:5A:8C:91:14:54:E2:A7:B4:51:5E:36:0C:52:CA:63:EC:04:10:A0:42:FF:46:E9:AD:05:B5:09:E1:87
```

## Features

Tubular enhances the NewPipe experience with:

- **SponsorBlock Integration**: Skip sponsored segments, intros, outros, and more
- **Return YouTube Dislike**: Restore visibility of dislike counts on YouTube videos
- **All NewPipe Features**: Ad-free video playback, background playback, downloads, subscriptions without account
- **Privacy-Focused**: No Google services or tracking

## Development Setup

### Prerequisites

- JDK 17 (OpenJDK recommended)
- Android SDK (API level 31)
- Android Studio or VS Code with Android extensions
- Git

### Configuration

1. Clone the repository:
   ```bash
   git clone https://github.com/polymorphicshade/Tubular.git
   cd Tubular
   ```

2. Create a `local.properties` file in the project root with:
   ```properties
   sdk.dir=path/to/android/sdk
   java.home=path/to/jdk17
   ```

3. For VS Code users, ensure your `launch.json` has the correct package and activity names:
   ```json
   "packageName": "org.polymorphicshade.tubular.debug",
   "activityName": "org.schabi.newpipe.MainActivity",
   "noDebug": true
   ```

### Build & Run

Build debug APK:
```bash
./gradlew :app:assembleDebug
```

Install on connected device:
```bash
adb install -r ./app/build/outputs/apk/debug/app-debug.apk
```

Launch app:
```bash
adb shell am start -n org.polymorphicshade.tubular.debug/org.schabi.newpipe.MainActivity
```

Run tests (excluding specific failing tests if needed):
```bash
./gradlew test
```

## Package Structure

Tubular maintains two important identifiers:

- **Internal Namespace**: `org.schabi.newpipe` (inherited from original NewPipe)
  - Used in imports, class definitions, and Java/Kotlin code
  
- **Application IDs**:
  - Release builds: `org.polymorphicshade.tubular` 
  - Debug builds: `org.polymorphicshade.tubular.debug`

When launching or configuring the app, both identifiers are important:
- Format: `[applicationId]/[namespace].[ActivityName]`
- Example: `org.polymorphicshade.tubular.debug/org.schabi.newpipe.MainActivity`

## Troubleshooting

**App Waiting for Debugger**
- In `app/build.gradle`, ensure `debuggable false` for the debug build variant
- For VS Code users, set `"noDebug": true` in launch.json

**JDK Configuration Issues**
- Ensure Java 17 is correctly referenced in both `local.properties` and `gradle.properties`
- Run `./gradlew --version` to verify Gradle is using the correct JDK

**Test Failures**
- Some test resources might be missing or require specific configuration
- Check test resources in `app/src/test/resources/` directory

**USB Connection Issues**
- Try different USB ports or cables
- Enable USB debugging in developer options
- Use `adb logcat > logcat.txt` for logging without relying on stable connection

## To Do
Things we'll be working on next (not in any particular order):
- [ ] Persist custom SponsorBlock segments in the database
- [ ] Add SponsorBlock's "Exclusive Access" / "Sponsored Video feature"
- [ ] Add SponsorBlock's chapters feature
- [ ] Add a clickbait-remover
- [ ] Add keyword/regex filtering
- [ ] Add subscription importing with a YouTube login cookie
- [ ] Add algorithmic results with a YouTube login cookie
- [ ] Add offline YouTube playback

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License
[![GNU GPLv3](https://www.gnu.org/graphics/gplv3-127x51.png)](https://www.gnu.org/licenses/gpl-3.0.en.html)
