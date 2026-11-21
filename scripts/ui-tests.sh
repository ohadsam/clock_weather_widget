#!/bin/sh
# Runs the instrumented UI tests (screenshots are copied by AGP to app/build/outputs/connected_android_test_additional_output/ even when a test fails).
# Used by .github/workflows/ui-tests.yml inside the emulator runner.
chmod +x gradlew
./gradlew :app:connectedDebugAndroidTest -PversionCodeOverride=1 --stacktrace
RESULT=$?
exit $RESULT
