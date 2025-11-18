@echo off
echo Spawner Sphere Mod - Build Script
echo =================================
echo.

REM Check if gradle wrapper exists
if not exist "gradlew.bat" (
    echo Generating Gradle wrapper...
    call gradle wrapper --gradle-version=8.5
)

REM Check command line argument
if "%1"=="" goto usage
if "%1"=="all" goto buildall
if "%1"=="legacy" goto buildlegacy
if "%1"=="1.19" goto build119
if "%1"=="1.20" goto build120
if "%1"=="1.21" goto build121
goto usage

:buildall
echo Building all versions...
echo.
call :build legacy-fabric "1.8.9-1.13.2"
call :build fabric-1.19 "1.19.x"
call :build fabric-1.20 "1.20.x"
call :build fabric-1.21 "1.21.x"
goto end

:buildlegacy
call :build legacy-fabric "1.8.9-1.13.2"
goto end

:build119
call :build fabric-1.19 "1.19.x"
goto end

:build120
call :build fabric-1.20 "1.20.x"
goto end

:build121
call :build fabric-1.21 "1.21.x"
goto end

:build
echo Building for Minecraft %~2...
call gradlew.bat :%~1:clean :%~1:build
if %errorlevel% equ 0 (
    echo [OK] Successfully built for Minecraft %~2
    echo      JAR location: %~1\build\libs\
) else (
    echo [ERROR] Failed to build for Minecraft %~2
)
echo.
goto :eof

:usage
echo Usage: build.bat [version]
echo.
echo Available versions:
echo   all    - Build all versions
echo   legacy - Build for 1.8.9-1.13.2
echo   1.19   - Build for 1.19.x
echo   1.20   - Build for 1.20.x
echo   1.21   - Build for 1.21.x
echo.
echo Example: build.bat 1.20
goto end

:end
echo Build process complete!
pause
