---
description: Visual process map for VAN mode (Initialization)
globs: van-mode-map.mdc
alwaysApply: false
---
# VAN MODE: INITIALIZATION PROCESS MAP

> **TL;DR:** This visual map defines the VAN mode process for project initialization, task analysis, and technical validation. It guides users through platform detection, file verification, complexity determination, and technical validation to ensure proper setup before implementation.

## 🧭 VAN MODE PROCESS FLOW

```mermaid
graph TD
    Start["START VAN MODE"] --> PlatformDetect["PLATFORM DETECTION"]
    PlatformDetect --> DetectOS["Detect Operating System"]
    DetectOS --> CheckPath["Check Path Separator Format"]
    CheckPath --> AdaptCmds["Adapt Commands if Needed"]
    AdaptCmds --> PlatformCP["⛔ PLATFORM CHECKPOINT"]
    
    %% Basic File Verification with checkpoint
    PlatformCP --> BasicFileVerify["BASIC FILE VERIFICATION"]
    BasicFileVerify --> BatchCheck["Batch Check Essential Components"]
    BatchCheck --> BatchCreate["Batch Create Essential Structure"]
    BatchCreate --> BasicFileCP["⛔ BASIC FILE CHECKPOINT"]
    
    %% Early Complexity Determination
    BasicFileCP --> EarlyComplexity["EARLY COMPLEXITY DETERMINATION"]
    EarlyComplexity --> AnalyzeTask["Analyze Task Requirements"]
    AnalyzeTask --> EarlyLevelCheck{"Complexity Level?"}
    
    %% Level handling paths
    EarlyLevelCheck -->|"Level 1"| ComplexityCP["⛔ COMPLEXITY CHECKPOINT"]
    EarlyLevelCheck -->|"Level 2-4"| CRITICALGATE["🚫 CRITICAL GATE: FORCE MODE SWITCH"]
    CRITICALGATE --> ForceExit["Exit to PLAN mode"]
    
    %% Level 1 continues normally
    ComplexityCP --> InitSystem["INITIALIZE MEMORY BANK"]
    InitSystem --> Complete1["LEVEL 1 INITIALIZATION COMPLETE"]
    
    %% For Level 2+ tasks after PLAN and CREATIVE modes
    ForceExit -.-> OtherModes["PLAN → CREATIVE modes"]
    OtherModes -.-> VANQA["VAN QA MODE"]
    VANQA --> QAProcess["Technical Validation Process"]
    QAProcess --> QACheck{"All Checks Pass?"}
    QACheck -->|"Yes"| BUILD["To BUILD MODE"]
    QACheck -->|"No"| FixIssues["Fix Technical Issues"]
    FixIssues --> QAProcess
    
    %% Style nodes
    style PlatformCP fill:#f55,stroke:#d44,color:white
    style BasicFileCP fill:#f55,stroke:#d44,color:white
    style ComplexityCP fill:#f55,stroke:#d44,color:white
    style CRITICALGATE fill:#ff0000,stroke:#990000,color:white,stroke-width:3px
    style ForceExit fill:#ff0000,stroke:#990000,color:white,stroke-width:2px
    style VANQA fill:#4da6ff,stroke:#0066cc,color:white,stroke-width:3px
    style QAProcess fill:#4da6ff,stroke:#0066cc,color:white
    style QACheck fill:#4da6ff,stroke:#0066cc,color:white
    style FixIssues fill:#ff5555,stroke:#dd3333,color:white
```

## 🌐 PLATFORM DETECTION PROCESS

```mermaid
graph TD
    PD["Platform Detection"] --> CheckOS["Detect Operating System"]
    CheckOS --> Win["Windows"]
    CheckOS --> Mac["macOS"]
    CheckOS --> Lin["Linux"]
    
    Win & Mac & Lin --> Adapt["Adapt Commands<br>for Platform"]
    
    Win --> WinPath["Path: Backslash (\\)"]
    Mac --> MacPath["Path: Forward Slash (/)"]
    Lin --> LinPath["Path: Forward Slash (/)"]
    
    Win --> WinCmd["Command Adaptations:<br>dir, icacls, etc."]
    Mac --> MacCmd["Command Adaptations:<br>ls, chmod, etc."]
    Lin --> LinCmd["Command Adaptations:<br>ls, chmod, etc."]
    
    WinPath & MacPath & LinPath --> PathCP["Path Separator<br>Checkpoint"]
    WinCmd & MacCmd & LinCmd --> CmdCP["Command<br>Checkpoint"]
    
    PathCP & CmdCP --> PlatformComplete["Platform Detection<br>Complete"]
    
    style PD fill:#4da6ff,stroke:#0066cc,color:white
    style PlatformComplete fill:#10b981,stroke:#059669,color:white
```

## 📁 FILE VERIFICATION PROCESS

```mermaid
graph TD
    FV["File Verification"] --> CheckFiles["Check Essential Files"]
    CheckFiles --> CheckMB["Check Memory Bank<br>Structure"]
    CheckMB --> MBExists{"Memory Bank<br>Exists?"}
    
    MBExists -->|"Yes"| VerifyMB["Verify Memory Bank<br>Contents"]
    MBExists -->|"No"| CreateMB["Create Memory Bank<br>Structure"]
    
    CheckFiles --> CheckDocs["Check Documentation<br>Files"]
    CheckDocs --> DocsExist{"Docs<br>Exist?"}
    
    DocsExist -->|"Yes"| VerifyDocs["Verify Documentation<br>Structure"]
    DocsExist -->|"No"| CreateDocs["Create Documentation<br>Structure"]
    
    VerifyMB & CreateMB --> MBCP["Memory Bank<br>Checkpoint"]
    VerifyDocs & CreateDocs --> DocsCP["Documentation<br>Checkpoint"]
    
    MBCP & DocsCP --> FileComplete["File Verification<br>Complete"]
    
    style FV fill:#4da6ff,stroke:#0066cc,color:white
    style FileComplete fill:#10b981,stroke:#059669,color:white
    style MBCP fill:#f6546a,stroke:#c30052,color:white
    style DocsCP fill:#f6546a,stroke:#c30052,color:white
```

## 🧩 COMPLEXITY DETERMINATION PROCESS

```mermaid
graph TD
    CD["Complexity<br>Determination"] --> AnalyzeTask["Analyze Task<br>Requirements"]
    
    AnalyzeTask --> CheckKeywords["Check Task<br>Keywords"]
    CheckKeywords --> ScopeCheck["Assess<br>Scope Impact"]
    ScopeCheck --> RiskCheck["Evaluate<br>Risk Level"]
    RiskCheck --> EffortCheck["Estimate<br>Implementation Effort"]
    
    EffortCheck --> DetermineLevel{"Determine<br>Complexity Level"}
    DetermineLevel -->|"Level 1"| L1["Level 1:<br>Quick Bug Fix"]
    DetermineLevel -->|"Level 2"| L2["Level 2:<br>Simple Enhancement"]
    DetermineLevel -->|"Level 3"| L3["Level 3:<br>Intermediate Feature"]
    DetermineLevel -->|"Level 4"| L4["Level 4:<br>Complex System"]
    
    L1 --> CDComplete["Complexity Determination<br>Complete"]
    L2 & L3 & L4 --> ModeSwitch["Force Mode Switch<br>to PLAN"]
    
    style CD fill:#4da6ff,stroke:#0066cc,color:white
    style CDComplete fill:#10b981,stroke:#059669,color:white
    style ModeSwitch fill:#ff0000,stroke:#990000,color:white
    style DetermineLevel fill:#f6546a,stroke:#c30052,color:white
```

## 🔄 COMPLETE WORKFLOW WITH QA VALIDATION

The full workflow includes technical validation before implementation:

```mermaid
flowchart LR
    VAN1["VAN MODE 
    (Initial Analysis)"] --> PLAN["PLAN MODE
    (Task Planning)"]
    PLAN --> CREATIVE["CREATIVE MODE
    (Design Decisions)"]
    CREATIVE --> VANQA["VAN QA MODE
    (Technical Validation)"] 
    VANQA --> BUILD["BUILD MODE
    (Implementation)"]
```

## 🔍 TECHNICAL VALIDATION OVERVIEW

The VAN QA technical validation process consists of four key validation points:

```mermaid
graph TD
    VANQA["VAN QA MODE"] --> FourChecks["FOUR-POINT VALIDATION"]
    
    FourChecks --> DepCheck["1️⃣ DEPENDENCY VERIFICATION<br>Check all required packages"]
    DepCheck --> ConfigCheck["2️⃣ CONFIGURATION VALIDATION<br>Verify format & compatibility"]
    ConfigCheck --> EnvCheck["3️⃣ ENVIRONMENT VALIDATION<br>Check build environment"]
    EnvCheck --> MinBuildCheck["4️⃣ MINIMAL BUILD TEST<br>Test core functionality"]
    
    MinBuildCheck --> ValidationResults{"All Checks<br>Passed?"}
    ValidationResults -->|"Yes"| SuccessReport["GENERATE SUCCESS REPORT"]
    ValidationResults -->|"No"| FailureReport["GENERATE FAILURE REPORT"]
    
    SuccessReport --> BUILD["Proceed to BUILD MODE"]
    FailureReport --> FixIssues["Fix Technical Issues"]
    FixIssues --> ReValidate["Re-validate"]
    ReValidate --> ValidationResults
    
    style VANQA fill:#4da6ff,stroke:#0066cc,color:white
    style FourChecks fill:#f6546a,stroke:#c30052,color:white
    style ValidationResults fill:#f6546a,stroke:#c30052,color:white
    style BUILD fill:#10b981,stroke:#059669,color:white
    style FixIssues fill:#ff5555,stroke:#dd3333,color:white
```

## 📝 VALIDATION STATUS FORMAT

The QA Validation step includes clear status indicators:

```
╔═════════════════ 🔍 QA VALIDATION STATUS ═════════════════╗
│ ✓ Design Decisions   │ Verified as implementable          │
│ ✓ Dependencies       │ All required packages installed    │
│ ✓ Configurations     │ Format verified for platform       │
│ ✓ Environment        │ Suitable for implementation        │
╚════════════════════════════════════════════════════════════╝
✅ VERIFIED - Clear to proceed to BUILD mode
```

## 🚨 MODE TRANSITION TRIGGERS

### VAN to PLAN Transition
For complexity levels 2-4:
```
🚫 LEVEL [2-4] TASK DETECTED
Implementation in VAN mode is BLOCKED
This task REQUIRES PLAN mode
You MUST switch to PLAN mode for proper documentation and planning
Type 'PLAN' to switch to planning mode
```

### CREATIVE to VAN QA Transition
After completing the CREATIVE mode:
```
⏭️ NEXT MODE: VAN QA
To validate technical requirements before implementation, please type 'VAN QA'
```

### VAN QA to BUILD Transition
After successful validation:
```
✅ TECHNICAL VALIDATION COMPLETE
All prerequisites verified successfully
You may now proceed to BUILD mode
Type 'BUILD' to begin implementation
```

## 🔒 BUILD MODE PREVENTION MECHANISM

The system prevents moving to BUILD mode without passing QA validation:

```mermaid
graph TD
    Start["User Types: BUILD"] --> CheckQA{"QA Validation<br>Completed?"}
    CheckQA -->|"Yes and Passed"| AllowBuild["Allow BUILD Mode"]
    CheckQA -->|"No or Failed"| BlockBuild["BLOCK BUILD MODE"]
    BlockBuild --> Message["Display:<br>⚠️ QA VALIDATION REQUIRED"]
    Message --> ReturnToVANQA["Prompt: Type VAN QA"]
    
    style CheckQA fill:#f6546a,stroke:#c30052,color:white
    style BlockBuild fill:#ff0000,stroke:#990000,color:white,stroke-width:3px
    style Message fill:#ff5555,stroke:#dd3333,color:white
    style ReturnToVANQA fill:#4da6ff,stroke:#0066cc,color:white
```

## 🔄 QA COMMAND PRECEDENCE

QA validation can be called at any point in the process flow, and takes immediate precedence over any other current steps, including forced mode switches:

```mermaid
graph TD
    UserQA["User Types: QA"] --> HighPriority["⚠️ HIGH PRIORITY COMMAND"]
    HighPriority --> CurrentTask["Pause Current Task/Process"]
    CurrentTask --> LoadQA["Load QA Mode Map"]
    LoadQA --> RunQA["Execute QA Validation Process"]
    RunQA --> QAResults{"QA Results"}
    
    QAResults -->|"PASS"| ResumeFlow["Resume Prior Process Flow"]
    QAResults -->|"FAIL"| FixIssues["Fix Identified Issues"]
    FixIssues --> ReRunQA["Re-run QA Validation"]
    ReRunQA --> QAResults
    
    style UserQA fill:#f8d486,stroke:#e8b84d,color:black
    style HighPriority fill:#ff0000,stroke:#cc0000,color:white,stroke-width:3px
    style LoadQA fill:#4da6ff,stroke:#0066cc,color:white
    style RunQA fill:#4da6ff,stroke:#0066cc,color:white
    style QAResults fill:#f6546a,stroke:#c30052,color:white
```

### QA Interruption Rules

When a user types **QA** at any point:

1. **The QA command MUST take immediate precedence** over any current operation, including the "FORCE MODE SWITCH" triggered by complexity assessment.
2. The system MUST:
   - Immediately load the QA mode map
   - Execute the full QA validation process
   - Address any failures before continuing
3. **Required remediation steps take priority** over any pending mode switches or complexity rules
4. After QA validation is complete and passes:
   - Resume the previously determined process flow
   - Continue with any required mode switches

```
⚠️ QA OVERRIDE ACTIVATED
All other processes paused
QA validation checks now running...
Any issues found MUST be remediated before continuing with normal process flow
```

## 📋 CHECKPOINT VERIFICATION TEMPLATE

Each major checkpoint in VAN mode uses this format:

```
✓ SECTION CHECKPOINT: [SECTION NAME]
- Requirement 1? [YES/NO]
- Requirement 2? [YES/NO]
- Requirement 3? [YES/NO]

→ If all YES: Ready for next section
→ If any NO: Fix missing items before proceeding
```

## 🚀 VAN MODE ACTIVATION

When the user types "VAN", respond with a confirmation and start the process:

```
User: VAN

Response: OK VAN - Beginning Initialization Process
```

After completing CREATIVE mode, when the user types "VAN QA", respond:

```
User: VAN QA

Response: OK VAN QA - Beginning Technical Validation
```

This ensures clear communication about which phase of VAN mode is active. 

## 🔍 DETAILED QA VALIDATION PROCESS

### 1️⃣ DEPENDENCY VERIFICATION

This step verifies that all required packages are installed and compatible:

```mermaid
graph TD
    Start["Dependency Verification"] --> ReadDeps["Read Required Dependencies<br>from Creative Phase"]
    ReadDeps --> CheckInstalled["Check if Dependencies<br>are Installed"]
    CheckInstalled --> DepStatus{"All Dependencies<br>Installed?"}
    
    DepStatus -->|"Yes"| VerifyVersions["Verify Versions<br>and Compatibility"]
    DepStatus -->|"No"| InstallMissing["Install Missing<br>Dependencies"]
    InstallMissing --> VerifyVersions
    
    VerifyVersions --> VersionStatus{"Versions<br>Compatible?"}
    VersionStatus -->|"Yes"| DepSuccess["Dependencies Verified<br>✅ PASS"]
    VersionStatus -->|"No"| UpgradeVersions["Upgrade/Downgrade<br>as Needed"]
    UpgradeVersions --> RetryVerify["Retry Verification"]
    RetryVerify --> VersionStatus
    
    style Start fill:#4da6ff,stroke:#0066cc,color:white
    style DepSuccess fill:#10b981,stroke:#059669,color:white
    style DepStatus fill:#f6546a,stroke:#c30052,color:white
    style VersionStatus fill:#f6546a,stroke:#c30052,color:white
```

#### Windows (PowerShell) Implementation:
```powershell
# Example: Verify Node.js dependencies for a React project
function Verify-Dependencies {
    $requiredDeps = @{
        "node" = ">=14.0.0"
        "npm" = ">=6.0.0"
    }
    
    $missingDeps = @()
    $incompatibleDeps = @()
    
    # Check Node.js version
    $nodeVersion = $null
    try {
        $nodeVersion = node -v
        if ($nodeVersion -match "v(\d+)\.(\d+)\.(\d+)") {
            $major = [int]$Matches[1]
            if ($major -lt 14) {
                $incompatibleDeps += "node (found $nodeVersion, required >=14.0.0)"
            }
        }
    } catch {
        $missingDeps += "node"
    }
    
    # Check npm version
    $npmVersion = $null
    try {
        $npmVersion = npm -v
        if ($npmVersion -match "(\d+)\.(\d+)\.(\d+)") {
            $major = [int]$Matches[1]
            if ($major -lt 6) {
                $incompatibleDeps += "npm (found $npmVersion, required >=6.0.0)"
            }
        }
    } catch {
        $missingDeps += "npm"
    }
    
    # Display results
    if ($missingDeps.Count -eq 0 -and $incompatibleDeps.Count -eq 0) {
        Write-Output "✅ All dependencies verified and compatible"
        return $true
    } else {
        if ($missingDeps.Count -gt 0) {
            Write-Output "❌ Missing dependencies: $($missingDeps -join ', ')"
        }
        if ($incompatibleDeps.Count -gt 0) {
            Write-Output "❌ Incompatible versions: $($incompatibleDeps -join ', ')"
        }
        return $false
    }
}
```

#### Mac/Linux (Bash) Implementation:
```bash
#!/bin/bash

# Example: Verify Node.js dependencies for a React project
verify_dependencies() {
    local missing_deps=()
    local incompatible_deps=()
    
    # Check Node.js version
    if command -v node &> /dev/null; then
        local node_version=$(node -v)
        if [[ $node_version =~ v([0-9]+)\.([0-9]+)\.([0-9]+) ]]; then
            local major=${BASH_REMATCH[1]}
            if (( major < 14 )); then
                incompatible_deps+=("node (found $node_version, required >=14.0.0)")
            fi
        fi
    else
        missing_deps+=("node")
    fi
    
    # Check npm version
    if command -v npm &> /dev/null; then
        local npm_version=$(npm -v)
        if [[ $npm_version =~ ([0-9]+)\.([0-9]+)\.([0-9]+) ]]; then
            local major=${BASH_REMATCH[1]}
            if (( major < 6 )); then
                incompatible_deps+=("npm (found $npm_version, required >=6.0.0)")
            fi
        fi
    else
        missing_deps+=("npm")
    fi
    
    # Display results
    if [ ${#missing_deps[@]} -eq 0 ] && [ ${#incompatible_deps[@]} -eq 0 ]; then
        echo "✅ All dependencies verified and compatible"
        return 0
    else
        if [ ${#missing_deps[@]} -gt 0 ]; then
            echo "❌ Missing dependencies: ${missing_deps[*]}"
        fi
        if [ ${#incompatible_deps[@]} -gt 0 ]; then
            echo "❌ Incompatible versions: ${incompatible_deps[*]}"
        fi
        return 1
    fi
}
```

### 2️⃣ CONFIGURATION VALIDATION

This step validates configuration files format and compatibility:

```mermaid
graph TD
    Start["Configuration Validation"] --> IdentifyConfigs["Identify Configuration<br>Files"]
    IdentifyConfigs --> ReadConfigs["Read Configuration<br>Files"]
    ReadConfigs --> ValidateSyntax["Validate Syntax<br>and Format"]
    ValidateSyntax --> SyntaxStatus{"Syntax<br>Valid?"}
    
    SyntaxStatus -->|"Yes"| CheckCompatibility["Check Compatibility<br>with Platform"]
    SyntaxStatus -->|"No"| FixSyntax["Fix Syntax<br>Errors"]
    FixSyntax --> RetryValidate["Retry Validation"]
    RetryValidate --> SyntaxStatus
    
    CheckCompatibility --> CompatStatus{"Compatible with<br>Platform?"}
    CompatStatus -->|"Yes"| ConfigSuccess["Configurations Validated<br>✅ PASS"]
    CompatStatus -->|"No"| AdaptConfigs["Adapt Configurations<br>for Platform"]
    AdaptConfigs --> RetryCompat["Retry Compatibility<br>Check"]
    RetryCompat --> CompatStatus
    
    style Start fill:#4da6ff,stroke:#0066cc,color:white
    style ConfigSuccess fill:#10b981,stroke:#059669,color:white
    style SyntaxStatus fill:#f6546a,stroke:#c30052,color:white
    style CompatStatus fill:#f6546a,stroke:#c30052,color:white
``` 

#### Configuration Validation Implementation:
```powershell
# Example: Validate configuration files for a web project
function Validate-Configurations {
    $configFiles = @(
        "package.json",
        "tsconfig.json",
        "vite.config.js"
    )
    
    $invalidConfigs = @()
    $incompatibleConfigs = @()
    
    foreach ($configFile in $configFiles) {
        if (Test-Path $configFile) {
            # Check JSON syntax for JSON files
            if ($configFile -match "\.json$") {
                try {
                    Get-Content $configFile -Raw | ConvertFrom-Json | Out-Null
                } catch {
                    $invalidConfigs += "$configFile (JSON syntax error: $($_.Exception.Message))"
                    continue
                }
            }
            
            # Specific configuration compatibility checks
            if ($configFile -eq "vite.config.js") {
                $content = Get-Content $configFile -Raw
                # Check for React plugin in Vite config
                if ($content -notmatch "react\(\)") {
                    $incompatibleConfigs += "$configFile (Missing React plugin for React project)"
                }
            }
        } else {
            $invalidConfigs += "$configFile (file not found)"
        }
    }
    
    # Display results
    if ($invalidConfigs.Count -eq 0 -and $incompatibleConfigs.Count -eq 0) {
        Write-Output "✅ All configurations validated and compatible"
        return $true
    } else {
        if ($invalidConfigs.Count -gt 0) {
            Write-Output "❌ Invalid configurations: $($invalidConfigs -join ', ')"
        }
        if ($incompatibleConfigs.Count -gt 0) {
            Write-Output "❌ Incompatible configurations: $($incompatibleConfigs -join ', ')"
        }
        return $false
    }
}
```

### 3️⃣ ENVIRONMENT VALIDATION

This step checks if the environment is properly set up for the implementation:

```mermaid
graph TD
    Start["Environment Validation"] --> CheckEnv["Check Build Environment"]
    CheckEnv --> VerifyBuildTools["Verify Build Tools"]
    VerifyBuildTools --> ToolsStatus{"Build Tools<br>Available?"}
    
    ToolsStatus -->|"Yes"| CheckPerms["Check Permissions<br>and Access"]
    ToolsStatus -->|"No"| InstallTools["Install Required<br>Build Tools"]
    InstallTools --> RetryTools["Retry Verification"]
    RetryTools --> ToolsStatus
    
    CheckPerms --> PermsStatus{"Permissions<br>Sufficient?"}
    PermsStatus -->|"Yes"| EnvSuccess["Environment Validated<br>✅ PASS"]
    PermsStatus -->|"No"| FixPerms["Fix Permission<br>Issues"]
    FixPerms --> RetryPerms["Retry Permission<br>Check"]
    RetryPerms --> PermsStatus
    
    style Start fill:#4da6ff,stroke:#0066cc,color:white
    style EnvSuccess fill:#10b981,stroke:#059669,color:white
    style ToolsStatus fill:#f6546a,stroke:#c30052,color:white
    style PermsStatus fill:#f6546a,stroke:#c30052,color:white
```

#### Environment Validation Implementation:
```powershell
# Example: Validate environment for a web project
function Validate-Environment {
    $requiredTools = @(
        @{Name = "git"; Command = "git --version"},
        @{Name = "node"; Command = "node --version"},
        @{Name = "npm"; Command = "npm --version"}
    )
    
    $missingTools = @()
    $permissionIssues = @()
    
    # Check build tools
    foreach ($tool in $requiredTools) {
        try {
            Invoke-Expression $tool.Command | Out-Null
        } catch {
            $missingTools += $tool.Name
        }
    }
    
    # Check write permissions in project directory
    try {
        $testFile = ".__permission_test"
        New-Item -Path $testFile -ItemType File -Force | Out-Null
        Remove-Item -Path $testFile -Force
    } catch {
        $permissionIssues += "Current directory (write permission denied)"
    }
    
    # Check if port 3000 is available (commonly used for dev servers)
    try {
        $listener = New-Object System.Net.Sockets.TcpListener([System.Net.IPAddress]::Loopback, 3000)
        $listener.Start()
        $listener.Stop()
    } catch {
        $permissionIssues += "Port 3000 (already in use or access denied)"
    }
    
    # Display results
    if ($missingTools.Count -eq 0 -and $permissionIssues.Count -eq 0) {
        Write-Output "✅ Environment validated successfully"
        return $true
    } else {
        if ($missingTools.Count -gt 0) {
            Write-Output "❌ Missing tools: $($missingTools -join ', ')"
        }
        if ($permissionIssues.Count -gt 0) {
            Write-Output "❌ Permission issues: $($permissionIssues -join ', ')"
        }
        return $false
    }
}
```

### 4️⃣ MINIMAL BUILD TEST

This step performs a minimal build test to ensure core functionality:

```mermaid
graph TD
    Start["Minimal Build Test"] --> CreateTest["Create Minimal<br>Test Project"]
    CreateTest --> BuildTest["Attempt<br>Build"]
    BuildTest --> BuildStatus{"Build<br>Successful?"}
    
    BuildStatus -->|"Yes"| RunTest["Run Basic<br>Functionality Test"]
    BuildStatus -->|"No"| FixBuild["Fix Build<br>Issues"]
    FixBuild --> RetryBuild["Retry Build"]
    RetryBuild --> BuildStatus
    
    RunTest --> TestStatus{"Test<br>Passed?"}
    TestStatus -->|"Yes"| TestSuccess["Minimal Build Test<br>✅ PASS"]
    TestStatus -->|"No"| FixTest["Fix Test<br>Issues"]
    FixTest --> RetryTest["Retry Test"]
    RetryTest --> TestStatus
    
    style Start fill:#4da6ff,stroke:#0066cc,color:white
    style TestSuccess fill:#10b981,stroke:#059669,color:white
    style BuildStatus fill:#f6546a,stroke:#c30052,color:white
    style TestStatus fill:#f6546a,stroke:#c30052,color:white
```

#### Minimal Build Test Implementation:
```powershell
# Example: Perform minimal build test for a React project
function Perform-MinimalBuildTest {
    $buildSuccess = $false
    $testSuccess = $false
    
    # Create minimal test project
    $testDir = ".__build_test"
    if (Test-Path $testDir) {
        Remove-Item -Path $testDir -Recurse -Force
    }
    
    try {
        # Create minimal test directory
        New-Item -Path $testDir -ItemType Directory | Out-Null
        Push-Location $testDir
        
        # Initialize minimal package.json
        @"
{
  "name": "build-test",
  "version": "1.0.0",
  "description": "Minimal build test",
  "main": "index.js",
  "scripts": {
    "build": "echo Build test successful"
  }
}
"@ | Set-Content -Path "package.json"
        
        # Attempt build
        npm run build | Out-Null
        $buildSuccess = $true
        
        # Create minimal test file
        @"
console.log('Test successful');
"@ | Set-Content -Path "index.js"
        
        # Run basic test
        node index.js | Out-Null
        $testSuccess = $true
        
    } catch {
        Write-Output "❌ Build test failed: $($_.Exception.Message)"
    } finally {
        Pop-Location
        if (Test-Path $testDir) {
            Remove-Item -Path $testDir -Recurse -Force
        }
    }
    
    # Display results
    if ($buildSuccess -and $testSuccess) {
        Write-Output "✅ Minimal build test passed successfully"
        return $true
    } else {
        if (-not $buildSuccess) {
            Write-Output "❌ Build process failed"
        }
        if (-not $testSuccess) {
            Write-Output "❌ Basic functionality test failed"
        }
        return $false
    }
}
```

## 📋 COMPREHENSIVE QA REPORT FORMAT

After running all validation steps, a comprehensive report is generated:

```
╔═════════════════════ 🔍 QA VALIDATION REPORT ══════════════════════╗
│                                                                     │
│  PROJECT: [Project Name]                                            │
│  TIMESTAMP: [Current Date/Time]                                     │
│                                                                     │
│  1️⃣ DEPENDENCY VERIFICATION                                         │
│  ✓ Required: [List of required dependencies]                        │
│  ✓ Installed: [List of installed dependencies]                      │
│  ✓ Compatible: [Yes/No]                                            │
│                                                                     │
│  2️⃣ CONFIGURATION VALIDATION                                        │
│  ✓ Config Files: [List of configuration files]                      │
│  ✓ Syntax Valid: [Yes/No]                                          │
│  ✓ Platform Compatible: [Yes/No]                                   │
│                                                                     │
│  3️⃣ ENVIRONMENT VALIDATION                                          │
│  ✓ Build Tools: [Available/Missing]                                │
│  ✓ Permissions: [Sufficient/Insufficient]                          │
│  ✓ Environment Ready: [Yes/No]                                     │
│                                                                     │
│  4️⃣ MINIMAL BUILD TEST                                              │
│  ✓ Build Process: [Successful/Failed]                              │
│  ✓ Functionality Test: [Passed/Failed]                             │
│  ✓ Build Ready: [Yes/No]                                           │
│                                                                     │
│  🚨 FINAL VERDICT: [PASS/FAIL]                                      │
│  ➡️ [Success message or error details]                              │
╚═════════════════════════════════════════════════════════════════════╝
```

## ❌ FAILURE REPORT FORMAT

If any validation step fails, a detailed failure report is generated:

```
⚠️⚠️⚠️ QA VALIDATION FAILED ⚠️⚠️⚠️

The following issues must be resolved before proceeding to BUILD mode:

1️⃣ DEPENDENCY ISSUES:
- [Detailed description of dependency issues]
- [Recommended fix]

2️⃣ CONFIGURATION ISSUES:
- [Detailed description of configuration issues]
- [Recommended fix]

3️⃣ ENVIRONMENT ISSUES:
- [Detailed description of environment issues]
- [Recommended fix]

4️⃣ BUILD TEST ISSUES:
- [Detailed description of build test issues]
- [Recommended fix]

⚠️ BUILD MODE IS BLOCKED until these issues are resolved.
Type 'VAN QA' after fixing the issues to re-validate.
```

## 🔄 INTEGRATION WITH DESIGN DECISIONS

The VAN QA mode reads and validates design decisions from the CREATIVE phase:

```mermaid
graph TD
    Start["Read Design Decisions"] --> ReadCreative["Parse Creative Phase<br>Documentation"]
    ReadCreative --> ExtractTech["Extract Technology<br>Choices"]
    ExtractTech --> ExtractDeps["Extract Required<br>Dependencies"]
    ExtractDeps --> BuildValidationPlan["Build Validation<br>Plan"]
    BuildValidationPlan --> StartValidation["Start Four-Point<br>Validation Process"]
    
    style Start fill:#4da6ff,stroke:#0066cc,color:white
    style ExtractTech fill:#f6546a,stroke:#c30052,color:white
    style BuildValidationPlan fill:#10b981,stroke:#059669,color:white
    style StartValidation fill:#f6546a,stroke:#c30052,color:white
```

### Technology Extraction Process:
```powershell
# Example: Extract technology choices from creative phase documentation
function Extract-TechnologyChoices {
    $techChoices = @{}
    
    # Read from systemPatterns.md
    if (Test-Path "memory-bank\systemPatterns.md") {
        $content = Get-Content "memory-bank\systemPatterns.md" -Raw
        
        # Extract framework choice
        if ($content -match "Framework:\s*(\w+)") {
            $techChoices["framework"] = $Matches[1]
        }
        
        # Extract UI library choice
        if ($content -match "UI Library:\s*(\w+)") {
            $techChoices["ui_library"] = $Matches[1]
        }
        
        # Extract state management choice
        if ($content -match "State Management:\s*([^\\n]+)") {
            $techChoices["state_management"] = $Matches[1].Trim()
        }
    }
    
    return $techChoices
}
```

## 🚨 IMPLEMENTATION PREVENTION MECHANISM

If QA validation fails, the system prevents moving to BUILD mode:

```powershell
# Example: Enforce QA validation before allowing BUILD mode
function Check-QAValidationStatus {
    $qaStatusFile = "memory-bank\.qa_validation_status"
    
    if (Test-Path $qaStatusFile) {
        $status = Get-Content $qaStatusFile -Raw
        if ($status -match "PASS") {
            return $true
        }
    }
    
    # Display block message
    Write-Output "`n`n"
    Write-Output "🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫"
    Write-Output "⛔️ BUILD MODE BLOCKED: QA VALIDATION REQUIRED"
    Write-Output "⛔️ You must complete QA validation before proceeding to BUILD mode"
    Write-Output "`n"
    Write-Output "Type 'VAN QA' to perform technical validation"
    Write-Output "`n"
    Write-Output "🚫 NO IMPLEMENTATION CAN PROCEED WITHOUT VALIDATION 🚫"
    Write-Output "🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫🚫"
    
    return $false
}
```

## 🧪 COMMON QA VALIDATION FIXES

Here are common fixes for issues encountered during QA validation:

### Dependency Issues:
- **Missing Node.js**: Install Node.js from https://nodejs.org/
- **Outdated npm**: Run `npm install -g npm@latest` to update
- **Missing packages**: Run `npm install` or `npm install [package-name]`

### Configuration Issues:
- **Invalid JSON**: Use a JSON validator to check syntax
- **Missing React plugin**: Add `import react from '@vitejs/plugin-react'` and `plugins: [react()]` to vite.config.js
- **Incompatible TypeScript config**: Update `tsconfig.json` with correct React settings

### Environment Issues:
- **Permission denied**: Run terminal as administrator (Windows) or use sudo (Mac/Linux)
- **Port already in use**: Kill process using the port or change the port in configuration
- **Missing build tools**: Install required command-line tools

### Build Test Issues:
- **Build fails**: Check console for specific error messages
- **Test fails**: Verify minimal configuration is correct
- **Path issues**: Ensure paths use correct separators for the platform

## 🔒 FINAL QA VALIDATION CHECKPOINT

```
✓ SECTION CHECKPOINT: QA VALIDATION
- Dependency Verification Passed? [YES/NO]
- Configuration Validation Passed? [YES/NO]
- Environment Validation Passed? [YES/NO]
- Minimal Build Test Passed? [YES/NO]

→ If all YES: Ready for BUILD mode
→ If any NO: Fix identified issues before proceeding
``` 