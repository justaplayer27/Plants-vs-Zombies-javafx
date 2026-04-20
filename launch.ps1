#!/usr/bin/env pwsh
# Plants vs Zombies Game Launcher (PowerShell)
# Compiles and launches the JavaFX game

Write-Host "========================================" -ForegroundColor Green
Write-Host "Plants vs Zombies Game Launcher"       -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

javac GameLauncher.java 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Failed to compile GameLauncher" -ForegroundColor Red
    exit 1
}

java GameLauncher
