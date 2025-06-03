# Style Guide: Tubular

## General Principles
- Maintain consistency with existing code patterns
- Follow Android platform best practices
- Balance between NewPipe's original style and Tubular-specific enhancements
- Prioritize readability and maintainability

## Java Code Style

### Formatting
- 4-space indentation
- Line length limit of 100 characters when possible
- Use Checkstyle for automatic style enforcement
- JavaDoc for public methods and classes

### Naming Conventions
- Classes: PascalCase (e.g., `VideoDetailFragment`)
- Methods/Variables: camelCase (e.g., `playVideo()`)
- Constants: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
- Package names: lowercase (e.g., `org.schabi.newpipe`)
- Layout XML files: lowercase_with_underscores (e.g., `activity_main.xml`)

### Class Organization
- Fields at the top
- Constructors after fields
- Public methods before private methods
- Related methods grouped together
- Static methods separated from instance methods

## Kotlin Code Style

### Formatting
- 4-space indentation
- Line length limit of 100 characters when possible
- Use KtLint for automatic formatting
- KDoc for public methods and classes

### Kotlin-Specific Guidelines
- Prefer val over var when possible
- Use data classes for simple data containers
- Use extension functions to enhance existing classes
- Use scope functions (let, apply, with, run, also) appropriately
- Use trailing lambda syntax when it improves readability

## XML Layout Style

### Structure
- Consistent attribute ordering
- One attribute per line for complex views
- Use styles and themes for reusable attributes
- Use include and merge tags to reduce duplication

### Naming
- IDs: view_type_purpose (e.g., `button_play`)
- Drawables: ic_action_name for icons, bg_component_name for backgrounds
- Colors: named by purpose, not value (e.g., `color_primary` not `blue_500`)

## Resource Management

### Strings
- All user-facing text in strings.xml
- Use string formatting for dynamic content
- Use plurals for quantity-dependent text

### Dimensions
- Reusable dimensions in dimens.xml
- Named by purpose (e.g., `margin_standard`, `text_size_title`)

### Colors
- Define in colors.xml
- Use semantic naming (e.g., `color_error` rather than `red`)

## Testing

### Unit Tests
- Test method naming: should_expectedBehavior_when_condition
- One assert per test when possible
- Use descriptive test method names
- Maintain independence between tests

### UI Tests
- Focus on critical user flows
- Use screen-based organization
- Clear naming that describes the scenario being tested

## Git Practices

### Commits
- Descriptive commit messages
- Start with verb in imperative form (e.g., "Add", "Fix", "Update")
- Reference issues/tasks in commit message when applicable

### Branches
- Feature branches named as `feature/short-description`
- Bug fix branches named as `fix/issue-description`
- Release branches named as `release/version-number`

## Documentation

### Code Comments
- Focus on "why" not "what"
- Document complex algorithms and business logic
- Keep comments up to date with code changes

### JavaDoc/KDoc
- Required for public API
- Describe parameters and return values
- Note exceptions and side effects

## Android-Specific Guidelines

### Lifecycle Management
- Handle configuration changes appropriately
- Clean up resources in appropriate lifecycle methods
- Use ViewModel for UI-related data

### Background Processing
- Use coroutines (Kotlin) or RxJava for asynchronous operations
- Avoid blocking the main thread
- Handle errors and edge cases gracefully

### UI Components
- Follow Material Design guidelines where appropriate
- Support different screen sizes
- Consider accessibility in UI design 