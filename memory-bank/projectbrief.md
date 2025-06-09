# Project Brief: Tubular

## Project Overview
Tubular is a fork of [NewPipe](https://newpipe.net/), a lightweight YouTube client for Android. Tubular extends NewPipe with additional features, primarily [SponsorBlock](https://sponsor.ajay.app/) for skipping sponsored content and [ReturnYouTubeDislike](https://www.returnyoutubedislike.com/) to restore dislike counts on videos.

## Project Goals
1. Maintain compatibility with the core NewPipe functionality
2. Enhance user experience with additional features:
   - Skip sponsored sections in videos
   - Display dislike counts that were removed from YouTube's interface
   - Add custom segments persistence in the database
   - Implement clickbait removal
   - Add keyword/regex filtering
   - Enable YouTube subscription importing with login cookies
   - Support algorithmic results with YouTube login cookies
   - Enable offline YouTube playback

## Technical Details
- **Framework**: Android native application (Java/Kotlin)
- **Base Project**: NewPipe
- **Key Integrations**: SponsorBlock and ReturnYouTubeDislike
- **Build System**: Gradle 8.9
- **Development Environment**: Cursor AI IDE on Windows
- **Testing Target**: Physical Android device (ID: 0I73C18I24101774)

## Current Status
The project is operational but faces several issues:
- Unit tests failing due to missing resources
- Launch configuration issues with package name mismatch
- Debugger hanging issues
- USB connectivity problems
- Permission issues due to project location in Program Files directory

## Next Steps
1. Resolve immediate launch and debugging issues
2. Fix unit tests and resource problems
3. Move project to a location with proper permissions
4. Continue implementing planned features from the to-do list 