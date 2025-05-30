# System Patterns: Tubular

## Architecture Overview
Tubular, as a fork of NewPipe, follows a modular architecture with separation of concerns between data extraction, UI components, and playback functionality.

## Core Components

### 1. Extractor Layer
- Custom fork of `NewPipeExtractor` library (`TubularExtractor`)
- Responsible for fetching and parsing content from YouTube and other services
- Abstracts service-specific APIs into a unified interface
- Uses service-specific classes for different platforms (YouTube, SoundCloud, etc.)

### 2. Database Layer
- Uses Room (AndroidX) for local storage
- Stores history, subscriptions, user preferences
- Handles feed updates and caching
- Will need extension for SponsorBlock segment persistence

### 3. UI Components
- Activity-Fragment pattern
- Uses AndroidX components (ViewModel, LiveData)
- Recycler views with adapter pattern for lists
- Player interface hierarchy for media playback

### 4. Media Playback
- Powered by ExoPlayer
- Supports multiple resolution formats
- PIP mode support
- Background playback capabilities
- Custom media controls

### 5. Download Management
- Component from "giga" for downloading media
- Custom download manager service
- Local file interaction

### 6. Service Integrations
- SponsorBlock API integration for sponsored content detection
- ReturnYouTubeDislike API for fetching dislike statistics

## Key Design Patterns

### 1. Repository Pattern
- Data access abstraction through repositories
- Separation between database, network, and UI

### 2. Observer Pattern
- LiveData for reactive UI updates
- RxJava for asynchronous operations
- Event bus for cross-component communication

### 3. Factory Pattern
- Service creation and initialization
- Player component creation

### 4. Dependency Injection
- Manual DI through constructors and factory methods
- No framework like Dagger/Hilt currently used

### 5. Builder Pattern
- Used for complex object creation
- Particularly for media format and player configurations

## File Structure Conventions
- Java/Kotlin mixed codebase
- Package by feature organization
- Resources in standard Android resource directories
- Gradle modules for separation of concerns

## Coding Standards
- Checkstyle for Java code style enforcement
- KtLint for Kotlin code formatting
- Tests for core functionality
- JavaDoc style comments for public APIs

## Extension Points
- Service connectors for adding new content sources
- Player implementation for custom playback behaviors
- Settings system for configuration
- Content filtering system (planned)

## Technical Debt Areas
- Mixed Java/Kotlin codebase
- Some deprecated Gradle configurations
- Test resources organization
- Legacy code from original NewPipe implementation 