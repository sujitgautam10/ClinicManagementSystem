#!/bin/bash
# ============================================================
#  Clinic Management System - Linux/Mac Build & Run Script
#  Week 9 OOP Architecture Submission
# ============================================================
#  EDIT THESE PATHS before running:
JAVAFX_PATH="/path/to/javafx-sdk/lib"
MYSQL_JAR="lib/mysql-connector-j-8.x.x.jar"
# ============================================================

echo "[1/3] Cleaning output directory..."
rm -rf out && mkdir out

echo "[2/3] Compiling sources..."
javac --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml \
  -cp "$MYSQL_JAR" \
  -d out \
  src/model/*.java \
  src/dao/*.java \
  src/service/*.java \
  src/util/*.java \
  src/ui/*.java \
  src/ClinicManagementApp.java

if [ $? -ne 0 ]; then
  echo "[ERROR] Compilation failed. Check JAVAFX_PATH and MYSQL_JAR paths."
  exit 1
fi

echo "[3/3] Running application..."
java --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml \
  -cp "out:$MYSQL_JAR" ClinicManagementApp
