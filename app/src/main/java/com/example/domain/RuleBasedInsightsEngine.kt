package com.example.domain

import androidx.compose.ui.graphics.Color
import com.example.data.model.DailyNote
import com.example.data.model.SymptomEntry
import com.example.data.model.SymptomType
import com.example.ui.theme.BurntOrange
import com.example.ui.theme.Mustard
import com.example.ui.theme.PlumLight
import com.example.ui.theme.PlumPrimary
import com.example.ui.theme.Sage

data class InsightItem(
    val id: String,
    val title: String,
    val headline: String,
    val metricBadge: String,
    val bodyText: String,
    val validatingNote: String, // Soft, warm, validating line: "This pattern is real and worth bringing to your doctor"
    val supportiveCare: String,
    val badgeColor: Color,
    val category: String
)

object RuleBasedInsightsEngine {

    fun generateInsights(
        entries: List<SymptomEntry>,
        dailyNotes: List<DailyNote>
    ): List<InsightItem> {
        val insights = mutableListOf<InsightItem>()
        if (entries.isEmpty()) {
            insights.add(
                InsightItem(
                    id = "empty_state",
                    title = "Building Your Rhythm",
                    headline = "Begin logging today to uncover your body's authentic patterns",
                    metricBadge = "GETTING STARTED",
                    bodyText = "Perimenopause symptoms fluctuate dynamically across days. As you log your daily reality, GirlyCycle calculates rule-based mathematical correlations between sleep, mental clarity, and body sensations.",
                    validatingNote = "Every sensation you log is valid and helps create clarity for your healthcare appointments.",
                    supportiveCare = "Log consecutive days to uncover correlations across your cycle.",
                    badgeColor = Mustard,
                    category = "Welcome"
                )
            )
            return insights
        }

        val poorSleepDates = entries.filter { it.symptomType == SymptomType.POOR_SLEEP }.map { it.date }.toSet()
        val brainFogDates = entries.filter { it.symptomType == SymptomType.BRAIN_FOG }.map { it.date }.toSet()
        val hotFlashDates = entries.filter { it.symptomType == SymptomType.HOT_FLASHES }.map { it.date }.toSet()
        val nightSweatDates = entries.filter { it.symptomType == SymptomType.NIGHT_SWEATS }.map { it.date }.toSet()
        val fatigueDates = entries.filter { it.symptomType == SymptomType.FATIGUE }.map { it.date }.toSet()
        val jointDates = entries.filter { it.symptomType == SymptomType.JOINT_PAIN }.map { it.date }.toSet()
        val rageDates = entries.filter { it.symptomType == SymptomType.MOOD_SWINGS_RAGE || it.symptomType == SymptomType.IRRITABILITY }.map { it.date }.toSet()

        // Rule 1: Poor Sleep & Brain Fog (Exact user requested example!)
        val sleepAndFogCoOccur = poorSleepDates.intersect(brainFogDates)
        if (brainFogDates.isNotEmpty()) {
            val coOccurCount = sleepAndFogCoOccur.size
            val totalFog = brainFogDates.size
            val ratio = ((coOccurCount.toFloat() / totalFog) * 100).toInt()

            insights.add(
                InsightItem(
                    id = "sleep_brain_fog",
                    title = "Sleep & Cognitive Rhythm",
                    headline = if (totalFog >= 3) {
                        "You logged poor sleep on $coOccurCount of the last $totalFog days you also logged brain fog"
                    } else {
                        "Poor sleep and brain fog co-occurred on $coOccurCount logged days"
                    },
                    metricBadge = "$ratio% LINKED",
                    bodyText = "When estrogen dips, brain glucose metabolism slows down and fragmented sleep prevents restorative REM cycles. Word-searching, losing track of conversations, and sluggish focus are physiological consequences of sleep debt.",
                    validatingNote = "This pattern is real and worth bringing to your doctor — you are not losing your mind, your brain is navigating rapid hormonal shifts.",
                    supportiveCare = "Protect high-focus tasks before noon and give yourself full grace for memory pauses in the afternoon.",
                    badgeColor = Mustard,
                    category = "Mind & Sleep"
                )
            )
        }

        // Rule 2: Hot Flashes & Broken Sleep
        val flashAndSleepCoOccur = hotFlashDates.intersect(poorSleepDates)
        if (hotFlashDates.size >= 2) {
            val coOccurCount = flashAndSleepCoOccur.size
            val totalFlash = hotFlashDates.size
            val ratioPercent = ((coOccurCount.toFloat() / totalFlash) * 100).toInt()

            insights.add(
                InsightItem(
                    id = "hot_flash_sleep",
                    title = "Temperature & Sleep Axis",
                    headline = "You logged poor sleep on $coOccurCount of the $totalFlash days with hot flashes",
                    metricBadge = "$ratioPercent% CORRELATED",
                    bodyText = "Nocturnal vasodilation releases adrenaline that jolts the nervous system awake, often leaving you alert at 3 AM with a pounding heart.",
                    validatingNote = "This pattern is real and worth bringing to your doctor — sleep disruption secondary to vasomotor spikes responds well to targeted hormonal or non-hormonal support.",
                    supportiveCare = "Keep bedroom ambient temperature at 65-67°F and use moisture-wicking natural linen bedding.",
                    badgeColor = BurntOrange,
                    category = "Temperature"
                )
            )
        }

        // Rule 3: Irritability / Peri-Rage & Broken Rest
        val sleepAndRageCoOccur = poorSleepDates.intersect(rageDates)
        if (rageDates.size >= 2) {
            val coOccur = sleepAndRageCoOccur.size
            val pct = ((coOccur.toFloat() / rageDates.size) * 100).toInt()

            insights.add(
                InsightItem(
                    id = "rage_sleep_connection",
                    title = "Emotional Bandwidth & Rest",
                    headline = "Sudden irritability & rage peaked on $coOccur days following broken sleep ($pct%)",
                    metricBadge = "VISCERAL PATTERN",
                    bodyText = "Perimenopause rage isn't a personality flaw. Estrogen modulates serotonin and GABA receptors. When compounded by broken sleep, your neurological sensory buffer is depleted.",
                    validatingNote = "You are not alone in feeling this sudden rage — this pattern is deeply biochemical and worth discussing openly with your doctor.",
                    supportiveCare = "Recognize sensory overload early; give yourself permission to step away from noise and demands.",
                    badgeColor = PlumLight,
                    category = "Emotional Rhythm"
                )
            )
        }

        // Rule 4: Fatigue & Joint Pain Co-occurrence
        val jointFatigueCoOccur = jointDates.intersect(fatigueDates)
        if (jointDates.size >= 2 && fatigueDates.isNotEmpty()) {
            val pct = ((jointFatigueCoOccur.size.toFloat() / jointDates.size) * 100).toInt()
            insights.add(
                InsightItem(
                    id = "joint_fatigue_cluster",
                    title = "Musculoskeletal & Cellular Energy",
                    headline = "Joint stiffness and high fatigue co-occurred on ${jointFatigueCoOccur.size} days ($pct%)",
                    metricBadge = "$pct% CO-OCCURRENCE",
                    bodyText = "Estrogen plays a key anti-inflammatory role in lubricating joint synovial fluid and sustaining mitochondrial energy. Dips in hormone levels produce morning stiffness alongside systemic fatigue.",
                    validatingNote = "This joint pain isn't just 'getting older' — it is a classic perimenopausal symptom that your doctor can evaluate.",
                    supportiveCare = "Gentle mobility stretching, warm baths with Epsom salts, and magnesium glycinate can support ease of movement.",
                    badgeColor = Sage,
                    category = "Body & Joints"
                )
            )
        }

        // Rule 5: User Precursor Trigger Correlations
        val allTriggers = entries.flatMap { entry ->
            entry.triggerList.map { trig -> trig to entry.severity }
        }
        val triggerGroups = allTriggers.groupBy { it.first }
        val prominentTrigger = triggerGroups.maxByOrNull { (_, list) ->
            list.count { it.second >= 3 }
        }
        if (prominentTrigger != null && prominentTrigger.value.size >= 2) {
            val triggerName = prominentTrigger.key
            val count = prominentTrigger.value.size

            insights.add(
                InsightItem(
                    id = "trigger_insight",
                    title = "Precursor Awareness",
                    headline = "You recorded \"$triggerName\" as a precursor on $count high-intensity days",
                    metricBadge = "TRIGGER CONTEXT",
                    bodyText = "In perimenopause, liver enzyme clearance shifts, making your body significantly more sensitive to external vasodilators and adrenaline stimulators.",
                    validatingNote = "Tracking your personal precursors empowers you with practical agency over your symptom triggers.",
                    supportiveCare = "Try experimenting with moderation or shifting timing for \"$triggerName\" to see if your symptom peaks soften.",
                    badgeColor = Mustard,
                    category = "Lifestyle Factors"
                )
            )
        }

        return insights
    }
}
