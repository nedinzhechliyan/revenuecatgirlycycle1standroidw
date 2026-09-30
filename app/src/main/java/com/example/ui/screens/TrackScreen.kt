package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyNote
import com.example.data.model.SymptomEntry
import com.example.data.model.SymptomType
import com.example.ui.components.CalendarStreakBar
import com.example.ui.components.EditorialHeader
import com.example.ui.components.SymptomCollectionCard
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.EditorialSerif
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import java.time.LocalDate

@Composable
fun TrackScreen(
    userName: String,
    userStage: String,
    selectedDate: LocalDate,
    loggedDates: Set<String>,
    streakCount: Int,
    daySymptoms: Map<SymptomType, SymptomEntry>,
    currentDailyNote: DailyNote?,
    topFrequentSymptoms: List<SymptomType>,
    isPremium: Boolean,
    saveFeedback: String?,
    onDateSelected: (LocalDate) -> Unit,
    onSelectToday: () -> Unit,
    onSelectYesterday: () -> Unit,
    onSeverityChange: (SymptomType, Int) -> Unit,
    onNotesChange: (SymptomType, String) -> Unit,
    onTriggerToggle: (SymptomType, String) -> Unit,
    onRemoveSymptom: (SymptomType) -> Unit,
    onDailyNoteUpdate: (flow: String, energy: Int, reflection: String) -> Unit,
    onUpdateName: (String) -> Unit,
    onOpenPaywall: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedSymptom by remember { mutableStateOf<SymptomType?>(null) }
    var showExtendedSection by remember { mutableStateOf(false) }

    // State for daily reflection / flow card
    var flowChoice by remember(currentDailyNote?.menstrualFlow) {
        mutableStateOf(currentDailyNote?.menstrualFlow ?: "None")
    }
    var energySlider by remember(currentDailyNote?.energyScore) {
        mutableIntStateOf(currentDailyNote?.energyScore ?: 3)
    }
    var reflectionText by remember(currentDailyNote?.generalReflection) {
        mutableStateOf(currentDailyNote?.generalReflection ?: "")
    }

    // Standard frequency-based primary symptoms (not hot flashes first)
    val primarySymptoms = remember {
        SymptomType.entries.filter { !it.isExtended }
    }

    // Extended symptoms placed behind progressive disclosure ("Add more")
    val extendedSymptoms = remember {
        SymptomType.entries.filter { it.isExtended }
    }

    val isSelectedToday = selectedDate == LocalDate.now()
    val isSelectedYesterday = selectedDate == LocalDate.now().minusDays(1)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("track_screen_scroll"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 96.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            EditorialHeader(
                userName = userName,
                userStage = userStage,
                selectedDate = selectedDate,
                isPremium = isPremium,
                onUpdateName = onUpdateName,
                onOpenPaywall = onOpenPaywall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Quick Day Switcher & "Log for Yesterday"
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TRACKING TIMELINE",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        )
                        if (streakCount > 0) {
                            Text(
                                text = "✨ $streakCount-day rhythm",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CoralPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Quick switch to Today
                        FilterChip(
                            selected = isSelectedToday,
                            onClick = { onSelectToday() },
                            label = { Text("Today (${LocalDate.now().monthValue}/${LocalDate.now().dayOfMonth})") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Today,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("chip_log_today")
                        )

                        // Quick switch to "Log for Yesterday"
                        FilterChip(
                            selected = isSelectedYesterday,
                            onClick = { onSelectYesterday() },
                            label = { Text("Log for Yesterday") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CoralPrimary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("chip_log_yesterday")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CalendarStreakBar(
                        selectedDate = selectedDate,
                        loggedDates = loggedDates,
                        streakCount = streakCount,
                        onDateSelected = onDateSelected,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Smart Defaults: 5 Most Commonly Logged Symptoms Shortcut Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = CoralPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "SMART DEFAULTS • YOUR FREQUENT SYMPTOMS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        topFrequentSymptoms.take(5).forEach { sym ->
                            val isLogged = daySymptoms.containsKey(sym)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isLogged) sym.badgeColor.copy(alpha = 0.25f)
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        1.dp,
                                        if (isLogged) sym.badgeColor else MaterialTheme.colorScheme.outline,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onRemoveSymptom(sym) }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = sym.displayName.split(" ").first(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isLogged) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isLogged) sym.badgeColor else MaterialTheme.colorScheme.onSurface
                                        ),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isLogged) "✓" else "+",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isLogged) sym.badgeColor else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Save Feedback Banner
        if (saveFeedback != null) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = TealPrimary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = saveFeedback,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }

        // Primary Symptoms Header
        item {
            Text(
                text = "Core Perimenopause Sensations",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = EditorialSerif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        // Frequency-ordered Primary Symptoms
        items(primarySymptoms, key = { it.name }) { symptomType ->
            val entry = daySymptoms[symptomType]
            val isExpanded = expandedSymptom == symptomType

            SymptomCollectionCard(
                symptomType = symptomType,
                entry = entry,
                isExpanded = isExpanded,
                onCardClick = {
                    expandedSymptom = if (isExpanded) null else symptomType
                },
                onSeverityChange = { newSev ->
                    onSeverityChange(symptomType, newSev)
                },
                onNotesChange = { newNotes ->
                    onNotesChange(symptomType, newNotes)
                },
                onTriggerToggle = { trig ->
                    onTriggerToggle(symptomType, trig)
                },
                onRemoveClick = {
                    onRemoveSymptom(symptomType)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Progressive Disclosure: "Add More" Section for Less-Common Symptoms
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showExtendedSection = !showExtendedSection }
                    .testTag("toggle_extended_symptoms"),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Add More Symptoms (Digestion, Skin, Intimacy...)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = if (showExtendedSection) "Tap to collapse less common symptoms" else "Less frequent symptoms kept out of your way",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Icon(
                        imageVector = if (showExtendedSection) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Extended Symptoms List (When Expanded)
        if (showExtendedSection) {
            items(extendedSymptoms, key = { it.name }) { symptomType ->
                val entry = daySymptoms[symptomType]
                val isExpanded = expandedSymptom == symptomType

                SymptomCollectionCard(
                    symptomType = symptomType,
                    entry = entry,
                    isExpanded = isExpanded,
                    onCardClick = {
                        expandedSymptom = if (isExpanded) null else symptomType
                    },
                    onSeverityChange = { newSev ->
                        onSeverityChange(symptomType, newSev)
                    },
                    onNotesChange = { newNotes ->
                        onNotesChange(symptomType, newNotes)
                    },
                    onTriggerToggle = { trig ->
                        onTriggerToggle(symptomType, trig)
                    },
                    onRemoveClick = {
                        onRemoveSymptom(symptomType)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Daily Reflection & Flow Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Daily Rhythm & Reflection",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Menstrual Flow Today",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("None", "Spotting", "Light", "Medium", "Heavy").forEach { flow ->
                            val isSelected = flowChoice.equals(flow, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CoralPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        flowChoice = flow
                                        onDailyNoteUpdate(flowChoice, energySlider, reflectionText)
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = flow,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Notes & Context",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = reflectionText,
                        onValueChange = {
                            reflectionText = it
                            onDailyNoteUpdate(flowChoice, energySlider, reflectionText)
                        },
                        placeholder = {
                            Text(
                                "Anything distinct about your day, food, exercise or stress...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("daily_note_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }
    }
}
