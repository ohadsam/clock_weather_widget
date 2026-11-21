# Releasing (summary - the full procedure and runbook are in `docs/OPERATIONS.md`)
1. Work on `main`; `gradle :core:test` green; docs updated in the same change (`docs/MAINTENANCE.md`).
2. Bump `appVersionName` in `app/build.gradle.kts` and add `## x.y.z` to `docs/CHANGELOG.md`.
3. Push. `android-ci.yml` (tests, lint, assemble) and `latest-apk.yml` (release APK, in-place-update verification, publish) must both be green; versionCode = 100 + commit count.
4. Verify the pre-release `latest`: `ClockWise.apk`, `ClockWise.apk.sha256`, `version.json` with the new version.
5. Install: open the APK from the release page (allow "install unknown apps" once) or use Settings -> Updates in the app. The committed `app/sideload.jks` is deliberately not secret; never rotate or delete it.
