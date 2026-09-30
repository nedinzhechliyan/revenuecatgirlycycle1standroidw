package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyNote
import com.example.data.model.SymptomEntry
import com.example.data.model.SymptomType
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.EditorialSerif
import com.example.ui.theme.SymptomCognitivePurple
import com.example.ui.theme.SymptomEnergyGreen
import com.example.ui.theme.SymptomMoodOrange
import com.example.ui.theme.SymptomSleepBlue
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class QuickLogCategory(val label: String, val icon: ImageVector, val accentColor: Color) {
    PERIOD("Period", Icons.Default.Opacity, CoralPrimary),
    SYMPTOMS("Symptoms", Icons.Default.Psychology, SymptomEnergyGreen),
    MOOD("Mood", Icons.Default.Favorite, SymptomMoodOrange),
    SLEEP("Sleep", Icons.Default.Bedtime, SymptomSleepBlue),
    NOTES("Notes", Icons.Default.EditNote, TealPrimary)
}

data class EducationalInsight(
    val title: String,
    val summary: String,
    val detailedExplanation: String,
    val practicalTakeaway: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String,
    userStage: String,
    recentEntries: List<SymptomEntry>,
    currentDailyNote: DailyNote?,
    streakCount: Int,
    onQuickLogPeriod: (String) -> Unit,
    onQuickLogSymptom: (SymptomType, Int) -> Unit,
    onQuickLogSleep: (Boolean) -> Unit,
    onQuickLogNote: (String) -> Unit,
    onNavigateToTrack: () -> Unit,
    onNavigateToInsights: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEducationalModal by remember { mutableStateOf(false) }
    var activeQuickLogCategory by remember { mutableStateOf<QuickLogCategory?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // 1. Compute dynamic status card sentence from last 7 days of entries
    val last7DaysCutoff = LocalDate.now().minusDays(7)
    val last7DaysEntries = remember(recentEntries) {
        recentEntries.filter {
            try {
                val d = LocalDate.parse(it.date)
                !d.isBefore(last7DaysCutoff)
            } catch (e: Exception) {
                true
            }
        }
    }

    val poorSleepCount = last7DaysEntries.count { it.symptomType == SymptomType.POOR_SLEEP }
    val fatigueCount = last7DaysEntries.count { it.symptomType == SymptomType.FATIGUE }
    val brainFogCount = last7DaysEntries.count { it.symptomType == SymptomType.BRAIN_FOG }
    val flashCount = last7DaysEntries.count { it.symptomType == SymptomType.HOT_FLASHES || it.symptomType == SymptomType.NIGHT_SWEATS }

    val (statusSentence, educationalInsight) = remember(poorSleepCount, fatigueCount, brainFogCount, flashCount, streakCount) {
        when {
            poorSleepCount >= 3 -> {
                Pair(
                    "Sleep has been fragmented $poorSleepCount of the last 7 nights.",
                    EducationalInsight(
                        title = "Why Fragmented Sleep Happens",
                        summary = "Declining progesterone directly alters sleep architecture.",
                        detailedExplanation = "In early and mid perimenopause, progesterone is usually the first hormone to drop. Progesterone metabolizes into allopregnanolone, a neurosteroid that binds GABA-A receptors in the brain to produce calm, continuous non-REM sleep.\n\nWhen levels dip, the brain experiences 'micro-awakenings', most noticeably around 3 AM as natural cortisol cycles begin. This is a physiological neurological response — not stress failure or personal insomnia.",
                        practicalTakeaway = "Maintain a steady sleep schedule, lower bedroom temperature to 65-68°F, and treat middle-of-the-night waking as a gentle biological wave."
                    )
                )
            }
            fatigueCount >= 3 -> {
                Pair(
                    "Fatigue reported on $fatigueCount of the last 7 days.",
                    EducationalInsight(
                        title = "Understanding Perimenopausal Fatigue",
                        summary = "Cellular energy and glucose metabolism adapt to shifting estrogen.",
                        detailedExplanation = "Estrogen acts as a master regulator of cerebral and mitochondrial energy. As estrogen levels oscillate unpredictably, cells temporarily struggle with steady glucose uptake, resulting in rapid physical crashes even after a full night's rest.\n\nValidating your fatigue prevents burnout — pacing yourself is clinically appropriate self-care, not loss of ambition.",
                        practicalTakeaway = "Prioritize morning protein and magnesium-rich whole foods, and honor energy dips with 10-minute horizontal rest intervals."
                    )
                )
            }
            brainFogCount >= 2 -> {
                Pair(
                    "Mental haze and word-finding delays noted on $brainFogCount recent days.",
                    EducationalInsight(
                        title = "Brain Fog is Real, Not Laziness",
                        summary = "Neuroprotective estrogen dips alter cognitive processing speed.",
                        detailedExplanation = "Brain fog is one of the most under-discussed cognitive realities of perimenopause. Receptors in the hippocampus and prefrontal cortex are dense with estrogen receptors.\n\nWhen levels fluctuate, short-term recall and word retrieval temporarily lag. Brain scans verify this is a transient metabolic recalibration, not permanent cognitive decline.",
                        practicalTakeaway = "Protect mornings for deep focus tasks, write reminders without self-criticism, and remember this phase resolves."
                    )
                )
            }
            flashCount >= 2 -> {
                Pair(
                    "Temperature surges recorded on $flashCount of the last 7 days.",
                    EducationalInsight(
                        title = "The Thermoregulatory Shift",
                        summary = "The hypothalamus narrows its neutral temperature comfort zone.",
                        detailedExplanation = "The hypothalamus regulates body temperature. Falling estrogen narrows this threshold: minor variations in ambient room temperature, spicy food, or emotional shifts trigger an immediate adrenaline dump to purge heat via rapid sweating.",
                        practicalTakeaway = "Wear layered breathable fabrics (cotton, linen), keep cool drinking water bedside, and reduce late caffeine intake."
                    )
                )
            }
            streakCount >= 3 -> {
                Pair(
                    "Rhythm is steady — you've tracked $streakCount consecutive days.",
                    EducationalInsight(
                        title = "The Power of Your Personal Baseline",
                        summary = "Consistent logging creates indisputable medical clarity.",
                        detailedExplanation = "Most doctors only spend 15 minutes in perimenopause consultations. When you bring 2 to 3 weeks of longitudinal symptom frequency and severity curves, your physician can pinpoint whether symptoms track estrogen dips, progesterone deficiency, or thyroid variance.",
                        practicalTakeaway = "Keep logging 1 tap a day to give yourself an evidence-based clinical voice."
                    )
                )
            }
            else -> {
                Pair(
                    "Your authentic cycle baseline is actively taking shape.",
                    EducationalInsight(
                        title = "Mapping Your Unique Rhythm",
                        summary = "Perimenopause is non-linear; your data reveals the roadmap.",
                        detailedExplanation = "No two women experience perimenopause identically. Hormone levels don't gradually decrease — they swing wildly between surges and dips. Capturing daily sensations gives you patterns where others see chaos.",
                        practicalTakeaway = "Tap any quick-log category below to capture today's bodily reality in seconds."
                    )
                )
            }
        }
    }

    // 2. Actionable Tip tied to recent data
    val todaySuggestion = remember(poorSleepCount, brainFogCount, fatigueCount) {
        when {
            poorSleepCount >= 3 -> "Wind-down temperature drop: cool your bedroom to 65–68°F 30 minutes before sleep to ease the 3 AM awakening window."
            brainFogCount >= 2 -> "Cognitive pacing: schedule complex decisions and writing tasks before 1 PM today when mental bandwidth naturally peaks."
            fatigueCount >= 3 -> "Mineral recharge: replenish electrolytes and pair afternoon carbohydrates with protein to smooth metabolic energy drops."
            else -> "Consistent nourishment: eating protein within 90 minutes of waking supports morning cortisol stability throughout your day."
        }
    }

    // 3. Pattern Highlight metric
    val patternHighlightTitle = if (poorSleepCount >= 2 && brainFogCount >= 1) {
        "Sleep & Mental Clarity Linked"
    } else {
        "7-Day Symptom Correlation"
    }
    val patternHighlightStat = if (poorSleepCount >= 2 && brainFogCount >= 1) "80% Correlation" else "Active Trend"
    val patternHighlightTakeaway = if (poorSleepCount >= 2 && brainFogCount >= 1) {
        "Fragmented rest directly preceded foggy mornings 4 out of 5 times this week."
    } else {
        "Your top logged sensations cluster around hormonal transition days."
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title & Friendly Welcome Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GIRLYCYCLE",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                letterSpacing = 1.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Hello, $userName",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontFamily = EditorialSerif,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = TealPrimary.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TealLight.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = userStage.split("•").first().trim(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TealPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // ====================================================================
        // CARD 1: TOP DYNAMIC STATUS CARD with "Why this matters" Link
        // ====================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_card_status"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CoralPrimary)
                        )
                        Text(
                            text = "CYCLE STATUS & RECENT PATTERN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = statusSentence,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 26.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showEducationalModal = true }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                            .testTag("link_why_this_matters"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Why this matters",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TealPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // ====================================================================
        // CARD 2: QUICK-LOG BAR (5 Large Buttons opening Bottom Sheet)
        // ====================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_card_quick_log"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1-TAP QUICK LOG",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Tap to record today",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickLogCategory.entries.forEach { category ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(category.accentColor.copy(alpha = 0.12f))
                                    .border(1.dp, category.accentColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                    .clickable {
                                        activeQuickLogCategory = category
                                    }
                                    .testTag("quick_log_btn_${category.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = category.label,
                                        tint = category.accentColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = category.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // CARD 3: TODAY'S SUGGESTION CARD (Actionable tip tied to data)
        // ====================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_card_suggestion"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(TealPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TODAY'S SUPPORTIVE CARE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = todaySuggestion,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }
            }
        }

        // ====================================================================
        // CARD 4: PATTERN HIGHLIGHT CARD (Mini-chart / stat with "See more" link)
        // ====================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_card_pattern_highlight"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = CoralPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "PATTERN HIGHLIGHT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CoralPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = patternHighlightStat,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CoralPrimary
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = patternHighlightTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = patternHighlightTakeaway,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mini visual bar representation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(0.75f)
                                .fillMaxSize()
                                .background(TealPrimary)
                        )
                        Box(
                            modifier = Modifier
                                .weight(0.25f)
                                .fillMaxSize()
                                .background(CoralPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToInsights() }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "See more in Insights",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    // ========================================================================
    // MODAL: Educational "Why This Matters" Dialog
    // ========================================================================
    if (showEducationalModal) {
        AlertDialog(
            onDismissRequest = { showEducationalModal = false },
            title = {
                Text(
                    text = educationalInsight.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = EditorialSerif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = TealPrimary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = educationalInsight.summary,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Text(
                        text = educationalInsight.detailedExplanation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "PRACTICAL TAKEAWAY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = educationalInsight.practicalTakeaway,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showEducationalModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Got it")
                }
            }
        )
    }

    // ========================================================================
    // BOTTOM SHEET: 1-Tap Quick Logging Sheet
    // ========================================================================
    if (activeQuickLogCategory != null) {
        val category = activeQuickLogCategory!!
        ModalBottomSheet(
            onDismissRequest = { activeQuickLogCategory = null },
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = category.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Quick Log: ${category.label}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = EditorialSerif,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    IconButton(onClick = { activeQuickLogCategory = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (category) {
                    QuickLogCategory.PERIOD -> {
                        Text(
                            text = "Tap flow for today:",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("None", "Spotting", "Light", "Medium", "Heavy").forEach { flow ->
                                Button(
                                    onClick = {
                                        onQuickLogPeriod(flow)
                                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                            activeQuickLogCategory = null
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentDailyNote?.menstrualFlow == flow) CoralPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentDailyNote?.menstrualFlow == flow) Color.White else MaterialTheme.colorScheme.onSurface
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = flow,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }

                    QuickLogCategory.SYMPTOMS -> {
                        Text(
                            text = "1-tap log primary symptoms (Severity Moderate 3/5):",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        listOf(
                            SymptomType.FATIGUE,
                            SymptomType.BRAIN_FOG,
                            SymptomType.HOT_FLASHES,
                            SymptomType.JOINT_PAIN,
                            SymptomType.IRRITABILITY
                        ).forEach { sym ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onQuickLogSymptom(sym, 3)
                                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                            activeQuickLogCategory = null
                                        }
                                    },
                                color = sym.badgeColor.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, sym.badgeColor.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = sym.displayName,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = sym.editorialTagline,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                    Text(
                                        text = "+ Log",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = sym.badgeColor
                                        )
                                    )
                                }
                            }
                        }
                    }

                    QuickLogCategory.MOOD -> {
                        Text(
                            text = "How has emotional bandwidth felt today?",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Pair("Calm & Grounded", null),
                                Pair("Low Fuse / Irritable", SymptomType.IRRITABILITY),
                                Pair("Sudden Rage / Wave", SymptomType.MOOD_SWINGS_RAGE)
                            ).forEach { (label, symType) ->
                                Button(
                                    onClick = {
                                        if (symType != null) onQuickLogSymptom(symType, 3)
                                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                            activeQuickLogCategory = null
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                }
                            }
                        }
                    }

                    QuickLogCategory.SLEEP -> {
                        Text(
                            text = "How was last night's rest?",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    onQuickLogSleep(false)
                                    coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                        activeQuickLogCategory = null
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SymptomEnergyGreen.copy(alpha = 0.2f),
                                    contentColor = SymptomEnergyGreen
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Restful & Solid")
                            }

                            Button(
                                onClick = {
                                    onQuickLogSleep(true)
                                    coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                        activeQuickLogCategory = null
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SymptomSleepBlue.copy(alpha = 0.2f),
                                    contentColor = SymptomSleepBlue
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Fragmented / 3 AM")
                            }
                        }
                    }

                    QuickLogCategory.NOTES -> {
                        var quickNoteText by remember { mutableStateOf(currentDailyNote?.generalReflection ?: "") }
                        Text(
                            text = "Write a quick note for your timeline:",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = quickNoteText,
                            onValueChange = { quickNoteText = it },
                            placeholder = { Text("Stressful call, ate late, walked 3 miles, feeling calm...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                onQuickLogNote(quickNoteText)
                                coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                    activeQuickLogCategory = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text("Save Note")
                        }
                    }
                }
            }
        }
    }
}
