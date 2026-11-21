#!/bin/sh
# Runs the instrumented UI tests, then ALWAYS pulls the screenshots (even when a test failed), and exits with the test result.
# Used by .github/workflows/ui-tests.yml inside the emulator runner.
chmod +x gradlew
./gradlew :app:connectedDebugAndroidTest -PversionCodeOverride=1 --stacktrace
RESULT=$?
mkdir -p shots
adb pull /sdcard/Android/data/com.clockweather.app/files/screenshots shots/ || true
exit $RESULT
