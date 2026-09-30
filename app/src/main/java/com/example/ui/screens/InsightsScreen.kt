package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.EntitlementState
import com.example.domain.InsightItem
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.EditorialSerif
import com.example.ui.theme.SymptomCognitivePurple
import com.example.ui.theme.SymptomEnergyGreen
import com.example.ui.theme.SymptomMoodOrange
import com.example.ui.theme.SymptomSleepBlue
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary

@Composable
fun InsightsScreen(
    insights: List<InsightItem>,
    entitlementState: EntitlementState,
    onOpenPaywall: () -> Unit,
    onOpenDoctorDossier: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val outlineColor = MaterialTheme.colorScheme.outline
    val isUnlocked = entitlementState.isInsightsUnlocked

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("insights_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!isUnlocked) {
            // Locked State Preview (trial expired & unsubscribed)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BODY INTELLIGENCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = CoralPrimary
                            )
                        )
                        TrialStatusLabel(entitlementState = entitlementState)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Patterns in Your Rhythm",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Rule-based non-ML mathematical links between your sleep, mental clarity, and body sensations.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Locked teaser card
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("insights_locked_card"),
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor),
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(CoralPrimary.copy(alpha = 0.2f))
                                    .border(1.dp, CoralPrimary.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = CoralPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "ATELIER MEMBERSHIP EXCLUSIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = CoralPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Body Correlation Engine",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = EditorialSerif,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Unlock cycle length history, symptom trends, and 2-3 rule-based correlations paired with actionable medical takeaways.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = onOpenPaywall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("upgrade_from_insights_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TealPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = CoralPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Upgrade to Atelier Membership",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        } else {
            // UNLOCKED STATE (During 21-day trial or active membership)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BODY INTELLIGENCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = CoralPrimary
                            )
                        )
                        TrialStatusLabel(entitlementState = entitlementState)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Patterns in Your Rhythm",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Actionable hormonal takeaways grounded in your daily logs — no raw numbers without context.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // ============================================================
                // 1. CYCLE OVERVIEW: Chart of cycle length over time + Plain-language callout
                // ============================================================
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CYCLE LENGTH HISTORY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TealPrimary,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = CoralPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "High Variability",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CoralPrimary,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Simple visual bars of cycle length over last 5 cycles
                            val cycleLengths = listOf(
                                Pair("May", 28),
                                Pair("Jun", 24),
                                Pair("Jul", 38),
                                Pair("Aug", 21),
                                Pair("Sep", 45)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                cycleLengths.forEach { (month, days) ->
                                    val barHeight = ((days / 50f) * 80).dp
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        Text(
                                            text = "${days}d",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (days > 35 || days < 24) CoralPrimary else TealPrimary
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .width(28.dp)
                                                .height(barHeight)
                                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                                .background(
                                                    if (days > 35 || days < 24) CoralPrimary else TealPrimary
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = month,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Plain-language callout for high variability
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = CoralPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Cycle length shifted by 24 days across recent cycles. High variability is very typical during perimenopause as ovulation occurs intermittently.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 21.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // ============================================================
                // 2. SYMPTOM TRENDS: Top 3 Symptoms with 1-line interpretation
                // ============================================================
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "TOP 3 SYMPTOM TRENDS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Top 1: Fatigue
                            TrendSymptomRow(
                                title = "1. Fatigue & Energy Dips",
                                badge = "Severity 3.8 / 5",
                                accentColor = SymptomEnergyGreen,
                                interpretation = "Energy dips most noticeably on days following broken sleep or estrogen shifts."
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Top 2: Poor Sleep
                            TrendSymptomRow(
                                title = "2. Fragmented Sleep & 3 AM Wake",
                                badge = "Severity 3.5 / 5",
                                accentColor = SymptomSleepBlue,
                                interpretation = "3 AM alertness tracks progesterone depletion; cool room temperature eases return to sleep."
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Top 3: Brain Fog
                            TrendSymptomRow(
                                title = "3. Brain Fog & Focus Latency",
                                badge = "Severity 3.2 / 5",
                                accentColor = SymptomCognitivePurple,
                                interpretation = "Recall lapses cluster mid-afternoon — a transient metabolic response, not cognitive loss."
                            )
                        }
                    }
                }

                // ============================================================
                // 3. CORRELATIONS: 2-3 Patterns Max with "Try this" suggestion
                // ============================================================
                item {
                    Text(
                        text = "Observed Patterns & Takeaways",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                items(insights.take(3), key = { it.id }) { insight ->
                    val tryThisSuggestion = when (insight.id) {
                        "sleep_brain_fog" -> "Wind down bedroom temperature to 65–68°F and protect mornings for high-focus decisions."
                        "hot_flash_sleep" -> "Keep a bedside ice pack or chilled water; sip non-caffeinated electrolytes in the afternoon."
                        else -> "Pair afternoon carbohydrates with protein to smooth metabolic energy dips."
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = insight.title.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Surface(
                                    color = insight.badgeColor.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = insight.metricBadge,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = insight.badgeColor
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = insight.headline,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = EditorialSerif,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 25.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = insight.bodyText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 22.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // "Try This" Suggestion Box
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = TealPrimary.copy(alpha = 0.1f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TealLight.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "TRY THIS",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TealPrimary,
                                                letterSpacing = 1.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = tryThisSuggestion,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Medium,
                                                lineHeight = 20.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ============================================================
                // 4. PROMINENT DOCTOR DOSSIER GENERATION BUTTON AT BOTTOM
                // ============================================================
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Take Your Evidence to Your Doctor",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = EditorialSerif,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Generate a formatted 30-day clinical report showing severity curves and frequency rankings for your next appointment.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onOpenDoctorDossier,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("insights_generate_doctor_report_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = "Generate Doctor Dossier",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun TrendSymptomRow(
    title: String,
    badge: String,
    accentColor: Color,
    interpretation: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = interpretation,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

/**
 * Backwards compatible overload taking isPremium boolean.
 */
@Composable
fun InsightsScreen(
    insights: List<InsightItem>,
    isPremium: Boolean,
    onOpenPaywall: () -> Unit,
    modifier: Modifier = Modifier
) = InsightsScreen(
    insights = insights,
    entitlementState = EntitlementState(
        isSubscribed = isPremium,
        isInsightsUnlocked = isPremium,
        isTrialActive = false
    ),
    onOpenPaywall = onOpenPaywall,
    onOpenDoctorDossier = {},
    modifier = modifier
)
