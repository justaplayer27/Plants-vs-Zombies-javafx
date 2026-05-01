# JavaFX SDK Installer and Game Launcher

$ProgressPreference = 'SilentlyContinue'
$ErrorActionPreference = 'Continue'

Write-Host "========================================" -ForegroundColor Green
Write-Host "JavaFX SDK Installer & Game Launcher" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

$JAVAFX_HOME = "C:\javafx-sdk"
$TEMP_ZIP = "$env:TEMP\javafx-sdk.zip"

# Check if already installed
if (Test-Path $JAVAFX_HOME) {
    Write-Host "JavaFX SDK found at $JAVAFX_HOME" -ForegroundColor Green
} else {
    Write-Host "JavaFX SDK not found. Downloading..." -ForegroundColor Yellow
    Write-Host ""
    
    $urls = @(
        "https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2-windows-x64-sdk.zip"
    )
    
    $downloaded = $false
    foreach ($url in $urls) {
        try {
            Write-Host "Downloading from: $url" -ForegroundColor Cyan
            Invoke-WebRequest -Uri $url -OutFile $TEMP_ZIP -UseBasicParsing -TimeoutSec 120
            if (Test-Path $TEMP_ZIP) {
                Write-Host "Download successful!" -ForegroundColor Green
                $downloaded = $true
                break
            }
        } catch {
            Write-Host "Download failed: $_" -ForegroundColor Red
            Write-Host "Trying alternative method..." -ForegroundColor Yellow
        }
    }
    
    if ($downloaded) {
        Write-Host "Extracting archive..." -ForegroundColor Cyan
        try {
            Expand-Archive -Path $TEMP_ZIP -DestinationPath "C:\" -Force
            if (Test-Path "C:\javafx-sdk-21.0.2") {
                Rename-Item -Path "C:\javafx-sdk-21.0.2" -NewName $JAVAFX_HOME -Force
            }
            Remove-Item $TEMP_ZIP -ErrorAction SilentlyContinue
            Write-Host "JavaFX installed successfully!" -ForegroundColor Green
        } catch {
            Write-Host "Extraction failed: $_" -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host ""
        Write-Host "ERROR: Could not download JavaFX SDK" -ForegroundColor Red
        Write-Host ""
        Write-Host "Manual Download Instructions:" -ForegroundColor Yellow
        Write-Host "1. Visit: https://gluonhq.com/products/javafx/" -ForegroundColor Cyan
        Write-Host "2. Download 'JavaFX Windows SDK' version 21" -ForegroundColor Cyan
        Write-Host "3. Extract to: C:\javafx-sdk" -ForegroundColor Cyan
        Write-Host "4. Run this script again" -ForegroundColor Cyan
        Write-Host ""
        exit 1
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Compiling Plants vs Zombies" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

$projectDir = Split-Path -Parent $PSScriptRoot
if (-not (Test-Path $projectDir)) {
    $projectDir = Get-Location
}

if (-not (Test-Path "$projectDir\bin")) {
    New-Item -ItemType Directory -Path "$projectDir\bin" | Out-Null
}

$javaFiles = Get-ChildItem -Recurse -Filter "*.java" -Path "$projectDir\src\main\java"
$errors = 0

foreach ($file in $javaFiles) {
    Write-Host "Compiling: $($file.Name)" -ForegroundColor Cyan
    & javac --module-path "$JAVAFX_HOME\lib" `
            --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media `
            -d "$projectDir\bin" `
            -cp "$projectDir\bin" `
            $file.FullName 2>&1 | ForEach-Object {
        if ($_ -match "error") {
            Write-Host "  ERROR: $_" -ForegroundColor Red
            $errors++
        } else {
            Write-Host "  $_" -ForegroundColor Gray
        }
    }
}

if ($errors -gt 0) {
    Write-Host ""
    Write-Host "Compilation failed with $errors errors!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Build successful!" -ForegroundColor Green
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Launching Plants vs Zombies" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

& java --module-path "$JAVAFX_HOME\lib" `
       --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media `
       -cp "$projectDir\bin" `
       com.pvz.game.PlantsVsZombiesApp

Write-Host ""
Write-Host "Game closed." -ForegroundColor Yellow
