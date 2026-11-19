# Releasing
1. Branch `claude/*` -> push -> `android-ci.yml` (core + app unit tests, lint, assemble debug+release, APK artifact).
2. Merge to `main` -> `latest-apk.yml` builds the release APK, **verifies it is an in-place update** of the published one (same applicationId, same signing certificate, higher versionCode) and republishes pre-release `latest` with `.sha256`.
3. versionCode = 100 + commit count (CI passes `-PversionCodeOverride`); versionName in `app/build.gradle.kts` (`val appVersionName`), with a matching `## x.y.z` in `docs/CHANGELOG.md`.
4. Install: open the APK from the release page on the phone (allow "install unknown apps" once). Later builds install over it.
5. The committed `app/sideload.jks` (password in `app/build.gradle.kts`) is deliberately not secret; never rotate or delete it.
