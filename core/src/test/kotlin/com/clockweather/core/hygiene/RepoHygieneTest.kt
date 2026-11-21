package com.clockweather.core.hygiene

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Repository rules enforced as tests: docs stay current, strings stay in sync, :core stays pure. Runs in the sandbox. */
class RepoHygieneTest {
    private val root: File = generateSequence(File("").absoluteFile) { it.parentFile }.first { File(it, "settings.gradle.kts").exists() }
    private fun read(path: String) = File(root, path).readText()

    @Test fun coreHasNoAndroidImports() {
        val offenders = File(root, "core/src/main").walkTopDown().filter { it.extension == "kt" }
            .filter { f -> f.readLines().any { it.startsWith("import android.") || it.startsWith("import androidx.") } }.map { it.name }.toList()
        assertTrue(offenders.isEmpty(), "Android imports in :core -> $offenders")
    }

    @Test fun coreNeverReadsTheClockOrPrintsOrSwallowsNothing() {
        val bad = Regex("""Instant\.now\(\)|LocalDate\.now\(\)|LocalDateTime\.now\(\)|System\.currentTimeMillis|println\(""")
        val hits = File(root, "core/src/main").walkTopDown().filter { it.extension == "kt" }
            .filter { bad.containsMatchIn(it.readText()) }.map { it.name }.toList()
        assertTrue(hits.isEmpty(), "time must be a parameter in :core -> $hits")
    }

    @Test fun everySourceFileIsInTheContextFileMap() {
        val ctx = read("docs/CONTEXT.md")
        val files = listOf("core/src/main", "core/src/test", "app/src/main/java", "app/src/test", "app/src/androidTest")
            .flatMap { d -> File(root, d).walkTopDown().filter { it.isFile && it.extension == "kt" }.toList() }
        val missing = files.map { it.name }.filterNot { ctx.contains(it) }
        assertTrue(missing.isEmpty(), "add to docs/CONTEXT.md file map: $missing")
    }

    @Test fun stringsExistInHebrewAndEnglishWithSamePlaceholders() {
        val key = Regex("""<string name="([a-z0-9_]+)"[^>]*>(.*?)</string>""", RegexOption.DOT_MATCHES_ALL)
        fun parse(p: String) = key.findAll(read(p)).associate { it.groupValues[1] to it.groupValues[2] }
        val he = parse("app/src/main/res/values/strings.xml"); val en = parse("app/src/main/res/values-en/strings.xml")
        assertEquals(he.keys, en.keys, "keys differ")
        val ph = Regex("""%\d\$[sd]""")
        for (k in he.keys) assertEquals(ph.findAll(he[k]!!).map { it.value }.sorted().toList(), ph.findAll(en[k]!!).map { it.value }.sorted().toList(), "placeholders of $k")
    }

    @Test fun everyStringResourceUsedInCodeExists() {
        val defined = Regex("""name="([a-z0-9_]+)"""").findAll(read("app/src/main/res/values/strings.xml")).map { it.groupValues[1] }.toSet()
        val used = File(root, "app/src/main/java").walkTopDown().filter { it.extension == "kt" }
            .flatMap { Regex("""R\.string\.([a-z0-9_]+)""").findAll(it.readText()).map { m -> m.groupValues[1] } }.toSet()
        assertTrue((used - defined).isEmpty(), "undefined strings: ${used - defined}")
    }

    @Test fun versionHasAChangelogSection() {
        val v = Regex("""val appVersionName = "([^"]+)"""").find(read("app/build.gradle.kts"))!!.groupValues[1]
        assertTrue(Regex("""(?m)^## ${Regex.escape(v)}\b""").containsMatchIn(read("docs/CHANGELOG.md")), "docs/CHANGELOG.md needs '## $v'")
    }

    @Test fun requestsLedgerAndPendingPromptsExist() {
        for (f in listOf("docs/REQUESTS.md", "docs/PENDING_CHATGPT_PROMPTS.md", "docs/DESIGN_LANGUAGE.md", "docs/PLAN.md", "AGENT.md", "CLAUDE.md")) assertTrue(File(root, f).isFile, f)
        val pending = read("docs/PENDING_CHATGPT_PROMPTS.md")
        for (p in File(root, "docs/prompts").listFiles()!!.filter { it.extension == "md" }) assertTrue(pending.contains(p.name), "${p.name} not registered in PENDING_CHATGPT_PROMPTS.md")
    }

    @Test fun signingKeyIsCommittedForInPlaceUpdates() {
        assertTrue(File(root, "app/sideload.jks").isFile)
    }

    @Test fun everyResourceXmlParsesAndEachWidgetReceiverHasItsOwnInfoFile() {
        val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }
        val xmls = File(root, "app/src/main/res").walkTopDown().filter { it.extension == "xml" }.toList() + File(root, "app/src/main/AndroidManifest.xml")
        for (f in xmls) runCatching { factory.newDocumentBuilder().parse(f) }.onFailure { throw AssertionError("${f.name} is not well-formed XML (duplicate attribute?): ${it.message}") }
        val infos = Regex("""android:resource="@xml/(widget_[a-z]+_info)"""").findAll(read("app/src/main/AndroidManifest.xml")).map { it.groupValues[1] }.toList()
        assertEquals(infos.size, infos.toSet().size, "two receivers share one appwidget-provider file: $infos")
        for (i in infos) assertTrue(File(root, "app/src/main/res/xml/$i.xml").isFile, "$i.xml missing")
    }

    // ---- documentation completeness (owner request 2026-11-21: docs must be maintained through the whole development) ----

    @Test fun everySettingsFieldIsDocumented() {
        val src = read("core/src/main/kotlin/com/clockweather/core/widget/WidgetSettings.kt")
        val body = src.substringAfter("data class WidgetSettings(").substringBefore(") {")
        val names = Regex("""(?m)^\s+val (\w+):""").findAll(body).map { it.groupValues[1] }.toList()
        val doc = read("docs/SETTINGS_REFERENCE.md")
        val missing = names.filterNot { doc.contains("`$it`") }
        assertTrue(names.size > 30 && missing.isEmpty(), "add to docs/SETTINGS_REFERENCE.md: $missing")
    }

    @Test fun everyDashboardCardIsInTheFeaturesDoc() {
        val ids = Regex("""\("([a-z_]+)", "[^"]*", "[^"]*"""").findAll(read("core/src/main/kotlin/com/clockweather/core/dashboard/Dashboard.kt")).map { it.groupValues[1] }.toList()
        val doc = read("docs/FEATURES.md")
        val missing = ids.filterNot { doc.contains("`$it`") }
        assertTrue(ids.size >= 14 && missing.isEmpty(), "add to docs/FEATURES.md card table: $missing")
    }

    @Test fun everyPermissionIsInThePrivacyDoc() {
        val perms = Regex("""uses-permission android:name="android\.permission\.([A-Z_]+)"""").findAll(read("app/src/main/AndroidManifest.xml")).map { it.groupValues[1] }.toSet()
        val doc = read("docs/DATA_AND_PRIVACY.md")
        val missing = perms.filterNot { doc.contains(it) }
        assertTrue(perms.size >= 8 && missing.isEmpty(), "add to docs/DATA_AND_PRIVACY.md: $missing")
    }

    @Test fun everyNetworkHostInCodeIsInThePrivacyDoc() {
        val hosts = File(root, "app/src/main").walkTopDown().plus(File(root, "core/src/main").walkTopDown()).filter { it.extension == "kt" }
            .flatMap { f -> Regex(""""([a-z0-9-]+(?:\.[a-z0-9-]+)+\.(?:com|org|net|io))"""").findAll(f.readText()).map { it.groupValues[1] } }.toSet()
        val doc = read("docs/DATA_AND_PRIVACY.md")
        val missing = hosts.filterNot { doc.contains(it) }
        assertTrue(hosts.isNotEmpty() && missing.isEmpty(), "add to docs/DATA_AND_PRIVACY.md: $missing")
    }

    @Test fun everyWorkflowIsInTheOperationsDoc() {
        val doc = read("docs/OPERATIONS.md")
        val missing = File(root, ".github/workflows").listFiles()!!.filter { it.extension == "yml" }.map { it.name }.filterNot { doc.contains(it) }
        assertTrue(missing.isEmpty(), "add to docs/OPERATIONS.md: $missing")
    }

    @Test fun everyDocIsLinkedFromTheReadme() {
        val readme = read("README.md")
        val missing = File(root, "docs").listFiles()!!.filter { it.extension == "md" }.map { it.name }.filterNot { readme.contains(it) }
        assertTrue(missing.isEmpty(), "link from README.md documentation map: $missing")
    }

    @Test fun everySeasonHasItsOrnamentArt() {
        val src = read("core/src/main/kotlin/com/clockweather/core/widget/Seasons.kt")
        val names = Regex(""""s_[a-z_]+", "[^"]*", "[^"]*", "([a-z_]+)"""").findAll(src).map { it.groupValues[1] }.toList()
        val missing = names.filterNot { File(root, "design/ornaments/originals/orn_$it.png").isFile && File(root, "app/src/main/res/drawable-nodpi/orn_$it.webp").isFile }
        assertTrue(names.size == 9 && missing.isEmpty(), "ornament art missing for: $missing")
    }
}
