package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.EntitlementState
import com.example.domain.DoctorReport
import com.example.domain.SymptomStat
import com.example.ui.components.TrendLineChart
import com.example.ui.theme.BurntOrange
import com.example.ui.theme.EditorialSerif
import com.example.ui.theme.Mustard
import com.example.ui.theme.PlumLight
import com.example.ui.theme.PlumPrimary
import com.example.ui.theme.Sage

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton

@Composable
fun DoctorReportScreen(
    report: DoctorReport,
    entitlementState: EntitlementState,
    onShareReport: (Context) -> Unit,
    onOpenPaywall: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val outlineColor = MaterialTheme.colorScheme.outline
    val isUnlocked = entitlementState.isDoctorDossierUnlocked

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("doctor_report_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!isUnlocked) {
            // Locked Preview State (trial expired & unsubscribed)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CLINICAL DOSSIER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = Mustard
                            )
                        )
                        TrialStatusLabel(entitlementState = entitlementState)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Doctor Dossier",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "A formatted 30-day report using your own logged tags and words, ready for your next doctor or gynecologist consultation.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Locked teaser CTA card matching Profile screen style
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dossier_locked_card"),
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
                                    .background(Mustard.copy(alpha = 0.2f))
                                    .border(1.dp, Mustard.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = Mustard,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "ATELIER MEMBERSHIP EXCLUSIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = Mustard
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "30-Day Doctor Dossier",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = EditorialSerif,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Unlock full symptom frequency rankings, 30-day cumulative severity curves, patient's own recorded notes, and one-tap Android share export.",
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
                                    .testTag("upgrade_from_dossier_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PlumPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Mustard,
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

                // Blurred / Teaser Preview of Trend Chart
                item {
                    Text(
                        text = "TEASER PREVIEW OF YOUR REPORT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    TrendLineChart(dataPoints = report.dailyTrendPoints)
                }

                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        } else {
            // Unlocked Doctor Dossier
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header & Share Action
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CLINICAL DOSSIER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = Mustard
                                )
                            )
                            Text(
                                text = "Doctor Dossier",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontFamily = EditorialSerif,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TrialStatusLabel(entitlementState = entitlementState)
                        }

                        Button(
                            onClick = { onShareReport(context) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PlumPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("share_report_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Share",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Date range & patient name pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Mustard,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = report.dateRangeDisplay.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Mustard,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }

                        Text(
                            text = "PATIENT: ${report.patientName.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                // 4 Summary Metric Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportMetricCard(
                            label = "DAYS LOGGED",
                            value = "${report.totalLoggedDays} / 30",
                            subtext = "${report.trackingConsistencyPercent}% consistency",
                            accentColor = Sage,
                            modifier = Modifier.weight(1f)
                        )
                        ReportMetricCard(
                            label = "MEAN SEVERITY",
                            value = "${report.averageOverallSeverity} / 5",
                            subtext = "Across logged days",
                            accentColor = BurntOrange,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    val topSymptom = report.symptomStats.firstOrNull()?.type?.displayName ?: "None"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportMetricCard(
                            label = "PRIMARY LOG",
                            value = topSymptom,
                            subtext = "${report.symptomStats.firstOrNull()?.occurrenceDays ?: 0} logged days",
                            accentColor = Mustard,
                            modifier = Modifier.weight(1f)
                        )
                        ReportMetricCard(
                            label = "CYCLE PATTERN",
                            value = if (report.cycleSummary.contains("No active")) "Variable" else "Active Flow",
                            subtext = "Perimenopause shift",
                            accentColor = PlumLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 30-Day Trend Chart
                item {
                    TrendLineChart(dataPoints = report.dailyTrendPoints)
                }

                // Ranked Symptoms Breakdown Header
                item {
                    Text(
                        text = "MOST FREQUENT SYMPTOMS (LAST 30 DAYS)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Mustard
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Ranked Symptom Items with user's own logged words & tags
                itemsIndexed(
                    items = report.symptomStats,
                    key = { _, stat -> stat.type.name }
                ) { index, stat ->
                    SymptomStatRowCard(index = index + 1, stat = stat)
                }

                // Menstrual Rhythm Dossier Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "MENSTRUAL RHYTHM & FLOW OBSERVATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = Mustard
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = report.cycleSummary,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }
                }

                // Observations & Notes for Doctor
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Mustard,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PATIENT OBSERVATIONS & NOTES FOR VISIT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = Mustard
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            report.keyClinicalObservations.forEach { obs ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        "•",
                                        color = PlumLight,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = obs,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 19.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Export CTA
                item {
                    Button(
                        onClick = { onShareReport(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("export_report_cta"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PlumPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Export & Share Report with Physician",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }
}

@Composable
private fun ReportMetricCard(
    label: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val outlineColor = MaterialTheme.colorScheme.outline
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp,
                    fontSize = 10.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = EditorialSerif,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    fontSize = 17.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun SymptomStatRowCard(
    index: Int,
    stat: SymptomStat
) {
    val outlineColor = MaterialTheme.colorScheme.outline
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$index.",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stat.type.displayName.uppercase(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Surface(
                    color = stat.type.badgeColor.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "AVG ${stat.averageSeverity} / 5",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = stat.type.badgeColor
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Frequency bar
            LinearProgressIndicator(
                progress = { (stat.frequencyPercentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = stat.type.badgeColor,
                trackColor = outlineColor.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${stat.occurrenceDays} days logged (${stat.frequencyPercentage}% of window)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "Peak: ${stat.maxSeverity}/5",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }

            // User's own logged triggers
            if (stat.topTriggers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Reported triggers: ${stat.topTriggers.joinToString(", ") { it.first }}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Mustard,
                        fontSize = 11.sp
                    )
                )
            }

            // User's own words (authentic personal notes)
            if (stat.sampleUserWords.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                stat.sampleUserWords.forEach { note ->
                    Text(
                        text = "In your words: \"$note\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = FontStyle.Italic,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }
        }
    }
}

/**
 * Subtle status badge displaying days left in the 21-day trial, active subscription, or expired state.
 */
@Composable
fun TrialStatusLabel(
    entitlementState: EntitlementState,
    modifier: Modifier = Modifier
) {
    if (entitlementState.isSubscribed) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Mustard.copy(alpha = 0.15f))
                .border(1.dp, Mustard.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Mustard,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Atelier Membership Active",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Mustard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
        }
    } else if (entitlementState.isTrialActive) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(PlumPrimary.copy(alpha = 0.25f))
                .border(1.dp, PlumLight.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Mustard)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (entitlementState.daysRemaining == 1) "1 day left in your free trial" else "${entitlementState.daysRemaining} days left in your free trial",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Mustard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
            )
        }
    } else {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(BurntOrange.copy(alpha = 0.18f))
                .border(1.dp, BurntOrange.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = BurntOrange,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Free trial ended • Upgrade to unlock",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BurntOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
        }
    }
}

/**
 * Backwards compatible overload taking isPremium boolean.
 */
@Composable
fun DoctorReportScreen(
    report: DoctorReport,
    isPremium: Boolean,
    onShareReport: (Context) -> Unit,
    onOpenPaywall: () -> Unit,
    modifier: Modifier = Modifier
) = DoctorReportScreen(
    report = report,
    entitlementState = EntitlementState(
        isSubscribed = isPremium,
        isDoctorDossierUnlocked = isPremium,
        isTrialActive = false
    ),
    onShareReport = onShareReport,
    onOpenPaywall = onOpenPaywall,
    modifier = modifier
)
