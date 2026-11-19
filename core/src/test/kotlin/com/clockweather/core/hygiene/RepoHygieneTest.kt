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
}
