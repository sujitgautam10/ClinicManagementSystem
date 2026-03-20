@echo off
REM ============================================================
REM  Clinic Management System - Windows Build & Run Script
REM  Week 9 OOP Architecture Submission
REM ============================================================
REM  EDIT THESE PATHS before running:
set JAVAFX_PATH=C:\path\to\javafx-sdk\lib
set MYSQL_JAR=lib\mysql-connector-j-8.x.x.jar
REM ============================================================

echo [1/3] Cleaning output directory...
if exist out rmdir /s /q out
mkdir out

echo [2/3] Compiling sources...
javac --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml ^
  -cp "%MYSQL_JAR%" ^
  -d out ^
  src\model\*.java ^
  src\dao\*.java ^
  src\service\*.java ^
  src\util\*.java ^
  src\ui\*.java ^
  src\ClinicManagementApp.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed. Check paths and Java version.
    pause
    exit /b 1
)

echo [3/3] Running application...
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml ^
  -cp "out;%MYSQL_JAR%" ClinicManagementApp

pause
