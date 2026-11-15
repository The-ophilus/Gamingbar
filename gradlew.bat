@echo off
setlocal
set "APP_HOME=%~dp0"
where gradle >nul 2>nul
if %ERRORLEVEL%==0 (
  gradle -p "%APP_HOME%" %*
  exit /b %ERRORLEVEL%
)
set "GRADLE_VERSION=8.9"
set "GRADLE_HOME=%USERPROFILE%\.gradle\wrapper\dists\gradle-%GRADLE_VERSION%-bin\manual\gradle-%GRADLE_VERSION%"
if not exist "%GRADLE_HOME%\bin\gradle.bat" (
  set "DIST_DIR=%USERPROFILE%\.gradle\wrapper\dists\gradle-%GRADLE_VERSION%-bin\manual"
  if not exist "%DIST_DIR%" mkdir "%DIST_DIR%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%DIST_DIR%\gradle.zip'; Expand-Archive -Force '%DIST_DIR%\gradle.zip' '%DIST_DIR%'"
  if errorlevel 1 exit /b 1
)
call "%GRADLE_HOME%\bin\gradle.bat" -p "%APP_HOME%" %*
exit /b %ERRORLEVEL%
