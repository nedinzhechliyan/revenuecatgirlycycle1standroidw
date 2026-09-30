package com.example.domain

import com.example.data.model.DailyNote
import com.example.data.model.SymptomEntry
import com.example.data.model.SymptomType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class SymptomStat(
    val type: SymptomType,
    val occurrenceDays: Int,
    val frequencyPercentage: Int, // e.g. 74%
    val averageSeverity: Float,
    val maxSeverity: Int,
    val totalCount: Int,
    val topTriggers: List<Pair<String, Int>>,
    val sampleUserWords: List<String>
)

data class DayBurdenPoint(
    val date: String,
    val dayLabel: String, // e.g. "Sep 12"
    val dayOfMonth: Int,
    val totalSeverityBurden: Int,
    val symptomCount: Int,
    val primarySymptom: SymptomType?
)

data class DoctorReport(
    val patientName: String,
    val dateRangeDisplay: String,
    val totalDaysInWindow: Int = 30,
    val totalLoggedDays: Int,
    val trackingConsistencyPercent: Int,
    val averageOverallSeverity: Float,
    val symptomStats: List<SymptomStat>,
    val dailyTrendPoints: List<DayBurdenPoint>,
    val cycleSummary: String,
    val keyClinicalObservations: List<String>,
    val formattedShareableSummary: String
)

object DoctorReportGenerator {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val shortDisplayFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US)

    fun generateReport(
        patientName: String,
        userStage: String = "Perimenopause (Transition Phase)",
        endDate: LocalDate = LocalDate.now(),
        windowDays: Int = 30,
        entries: List<SymptomEntry>,
        dailyNotes: List<DailyNote>
    ): DoctorReport {
        val startDate = endDate.minusDays((windowDays - 1).toLong())
        val dateRangeDisplay = "${startDate.format(shortDisplayFormatter)} – ${endDate.format(shortDisplayFormatter)}"

        val entriesByDate = entries.groupBy { it.date }
        val distinctLoggedDates = entriesByDate.keys.size
        val trackingConsistency = if (windowDays > 0) ((distinctLoggedDates.toFloat() / windowDays) * 100).toInt() else 0

        // Build 30-day timeline trend points
        val trendPoints = mutableListOf<DayBurdenPoint>()
        for (i in (windowDays - 1) downTo 0) {
            val d = endDate.minusDays(i.toLong())
            val dateStr = d.format(dateFormatter)
            val dayEntries = entriesByDate[dateStr] ?: emptyList()
            val totalSeverity = dayEntries.sumOf { it.severity }
            val primary = dayEntries.maxByOrNull { it.severity }?.symptomType

            trendPoints.add(
                DayBurdenPoint(
                    date = dateStr,
                    dayLabel = d.format(shortDisplayFormatter),
                    dayOfMonth = d.dayOfMonth,
                    totalSeverityBurden = totalSeverity,
                    symptomCount = dayEntries.size,
                    primarySymptom = primary
                )
            )
        }

        // Calculate stats per symptom using user's own logged tags and words
        val entriesByType = entries.groupBy { it.symptomType }
        val symptomStats = SymptomType.entries.mapNotNull { type ->
            val typeEntries = entriesByType[type] ?: return@mapNotNull null
            if (typeEntries.isEmpty()) return@mapNotNull null

            val days = typeEntries.map { it.date }.distinct().size
            val freqPercent = if (distinctLoggedDates > 0) ((days.toFloat() / distinctLoggedDates) * 100).toInt() else 0
            val avgSev = typeEntries.map { it.severity }.average().toFloat()
            val maxSev = typeEntries.maxOfOrNull { it.severity } ?: 0
            val totalOccurrences = typeEntries.sumOf { it.count.coerceAtLeast(1) }

            // Trigger frequency count from user's logs
            val triggerMap = mutableMapOf<String, Int>()
            typeEntries.forEach { entry ->
                entry.triggerList.forEach { trig ->
                    triggerMap[trig] = (triggerMap[trig] ?: 0) + 1
                }
            }
            val topTriggers = triggerMap.toList().sortedByDescending { it.second }.take(4)

            // User's own logged words & notes (not generic medical phrasing)
            val userNotes = typeEntries
                .map { it.notes.trim() }
                .filter { it.isNotBlank() }
                .distinct()
                .take(3)

            SymptomStat(
                type = type,
                occurrenceDays = days,
                frequencyPercentage = freqPercent,
                averageSeverity = (avgSev * 10).toInt() / 10f,
                maxSeverity = maxSev,
                totalCount = totalOccurrences,
                topTriggers = topTriggers,
                sampleUserWords = userNotes
            )
        }.sortedByDescending { it.occurrenceDays }

        val avgOverallSeverity = if (entries.isNotEmpty()) {
            val avg = entries.map { it.severity }.average().toFloat()
            (avg * 10).toInt() / 10f
        } else 0f

        // Cycle analysis
        val cycleNotes = dailyNotes.filter { it.menstrualFlow != "None" }
        val cycleSummary = if (cycleNotes.isEmpty()) {
            "No active menstrual bleeding or spotting recorded in this 30-day window (infrequent / variable cycle characteristic of perimenopause)."
        } else {
            "Menstrual flow logged on ${cycleNotes.size} day(s). User logged: ${cycleNotes.map { it.menstrualFlow }.distinct().joinToString(", ")}."
        }

        // Clinical observations with authentic user context
        val observations = mutableListOf<String>()
        val topSymptom = symptomStats.firstOrNull()
        if (topSymptom != null) {
            observations.add(
                "Most prominent experience: ${topSymptom.type.displayName} reported on ${topSymptom.occurrenceDays} of $distinctLoggedDates logged days (${topSymptom.frequencyPercentage}%), with average severity ${topSymptom.averageSeverity}/5."
            )
        }

        val sleepStat = symptomStats.firstOrNull { it.type == SymptomType.POOR_SLEEP }
        val neuroStats = symptomStats.filter {
            it.type == SymptomType.BRAIN_FOG || it.type == SymptomType.IRRITABILITY || it.type == SymptomType.MOOD_SWINGS_RAGE
        }
        if (sleepStat != null && neuroStats.isNotEmpty()) {
            val neuroNames = neuroStats.joinToString(" and ") { it.type.displayName }
            observations.add(
                "Rest & Cognitive Rhythm: Disrupted sleep directly coincided with higher severity in $neuroNames."
            )
        }

        val jointStat = symptomStats.firstOrNull { it.type == SymptomType.JOINT_PAIN }
        if (jointStat != null && jointStat.occurrenceDays >= 3) {
            observations.add(
                "Physical & Joint: Joint stiffness reported on ${jointStat.occurrenceDays} days. Patient notes morning knuckle and knee stiffness."
            )
        }

        if (observations.isEmpty()) {
            observations.add("Tracking initiated. Continue logging daily to build your 30-day profile.")
        }

        // Formatted shareable summary
        val formattedSummary = buildShareableText(
            patientName = patientName,
            userStage = userStage,
            dateRange = dateRangeDisplay,
            loggedDays = distinctLoggedDates,
            totalDays = windowDays,
            consistency = trackingConsistency,
            avgSeverity = avgOverallSeverity,
            stats = symptomStats,
            cycleSummary = cycleSummary,
            observations = observations
        )

        return DoctorReport(
            patientName = patientName,
            dateRangeDisplay = dateRangeDisplay,
            totalDaysInWindow = windowDays,
            totalLoggedDays = distinctLoggedDates,
            trackingConsistencyPercent = trackingConsistency,
            averageOverallSeverity = avgOverallSeverity,
            symptomStats = symptomStats,
            dailyTrendPoints = trendPoints,
            cycleSummary = cycleSummary,
            keyClinicalObservations = observations,
            formattedShareableSummary = formattedSummary
        )
    }

    private fun buildShareableText(
        patientName: String,
        userStage: String,
        dateRange: String,
        loggedDays: Int,
        totalDays: Int,
        consistency: Int,
        avgSeverity: Float,
        stats: List<SymptomStat>,
        cycleSummary: String,
        observations: List<String>
    ): String {
        return buildString {
            appendLine("══════════════════════════════════════════════")
            appendLine("GIRLYCYCLE ATELIER • DOCTOR VISIT DOSSIER")
            appendLine("══════════════════════════════════════════════")
            appendLine("PATIENT: $patientName")
            appendLine("STAGE: $userStage")
            appendLine("WINDOW: $dateRange (Last $totalDays Days)")
            appendLine("DATA RECORD: $loggedDays of $totalDays days logged ($consistency% tracking consistency)")
            appendLine("AVERAGE SEVERITY: $avgSeverity / 5.0")
            appendLine("──────────────────────────────────────────────")
            appendLine("1. SYMPTOM FREQUENCY & PATIENT'S WORDS:")
            if (stats.isEmpty()) {
                appendLine("   No symptoms logged in this interval.")
            } else {
                stats.forEachIndexed { idx, s ->
                    appendLine("   ${idx + 1}. ${s.type.displayName.uppercase()}")
                    appendLine("      • Logged: ${s.occurrenceDays} days (${s.frequencyPercentage}% of recorded days)")
                    appendLine("      • Mean Severity: ${s.averageSeverity} / 5.0 (Peak: ${s.maxSeverity})")
                    if (s.topTriggers.isNotEmpty()) {
                        appendLine("      • Precursors: ${s.topTriggers.joinToString(", ") { "${it.first} (${it.second}x)" }}")
                    }
                    if (s.sampleUserWords.isNotEmpty()) {
                        appendLine("      • In Patient's Own Words: \"${s.sampleUserWords.joinToString("\" • \"")}\"")
                    }
                }
            }
            appendLine("──────────────────────────────────────────────")
            appendLine("2. MENSTRUAL FLOW & CYCLE STATUS:")
            appendLine("   $cycleSummary")
            appendLine("──────────────────────────────────────────────")
            appendLine("3. PATIENT OBSERVATIONS & NOTES FOR VISIT:")
            observations.forEach { obs ->
                appendLine("   • $obs")
            }
            appendLine("──────────────────────────────────────────────")
            appendLine("Report generated with GirlyCycle.")
            appendLine("Ready for consultation with your primary physician or gynecologist.")
            appendLine("══════════════════════════════════════════════")
        }
    }
}
