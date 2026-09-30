package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SymptomEntry
import com.example.data.model.SymptomType
import com.example.ui.theme.BurntOrange
import com.example.ui.theme.EditorialSerif
import com.example.ui.theme.Mustard
import com.example.ui.theme.PlumLight
import com.example.ui.theme.PlumPrimary
import com.example.ui.theme.Sage
import com.example.ui.theme.Severity1Color
import com.example.ui.theme.Severity2Color
import com.example.ui.theme.Severity3Color
import com.example.ui.theme.Severity4Color
import com.example.ui.theme.Severity5Color

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SymptomCollectionCard(
    symptomType: SymptomType,
    entry: SymptomEntry?,
    isExpanded: Boolean,
    onCardClick: () -> Unit,
    onSeverityChange: (Int) -> Unit,
    onNotesChange: (String) -> Unit,
    onTriggerToggle: (String) -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLogged = entry != null
    val currentSeverity = entry?.severity ?: 3
    var sliderValue by remember(entry?.severity) {
        mutableFloatStateOf(currentSeverity.toFloat())
    }
    var notesText by remember(entry?.notes) {
        mutableStateOf(entry?.notes ?: "")
    }

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "arrow_rotation"
    )

    // Dynamic accent color based on severity (1-5)
    val severityAccentColor = when (sliderValue.toInt().coerceIn(1, 5)) {
        1 -> Severity1Color
        2 -> Severity2Color
        3 -> Severity3Color
        4 -> Severity4Color
        else -> Severity5Color
    }

    val outlineColor = MaterialTheme.colorScheme.outline
    val activeCardBorderColor by animateColorAsState(
        targetValue = if (isLogged) symptomType.badgeColor else if (isExpanded) PlumLight else outlineColor,
        label = "border_color"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("symptom_card_${symptomType.name.lowercase()}"),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isExpanded || isLogged) 1.5.dp else 1.dp,
            color = activeCardBorderColor
        ),
        shadowElevation = if (isExpanded) 6.dp else 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCardClick() }
                .padding(20.dp)
        ) {
            // Header collection row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Collection tag e.g. "№ 01 • VITALITY & SLEEP"
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(symptomType.badgeColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${symptomType.collectionNumber} • ${symptomType.category.label.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Status pill
                if (isLogged) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(severityAccentColor.copy(alpha = 0.18f))
                            .border(1.dp, severityAccentColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SEVERITY ${entry.severity} / 5",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = severityAccentColor
                            )
                        )
                    }
                } else {
                    Text(
                        text = "TAP TO LOG",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        ),
                        modifier = Modifier
                            .border(1.dp, outlineColor, RoundedCornerShape(14.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main headline & editorial subtitle (identical weight for neuro & physical)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = symptomType.displayName.uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = symptomType.editorialTagline,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    )
                }

                IconButton(
                    onClick = { onCardClick() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand details",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.rotate(arrowRotation)
                    )
                }
            }

            // Expanded Editor Section
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(outlineColor.copy(alpha = 0.5f))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Severity Description
                    val severityDescription = when (sliderValue.toInt().coerceIn(1, 5)) {
                        1 -> "1 • Mild / Noticeable (Awareness without day disruption)"
                        2 -> "2 • Moderate (Clear discomfort, manageable)"
                        3 -> "3 • Pronounced (Definite interruption of normal rhythm)"
                        4 -> "4 • Severe (Disruptive, requires immediate self-care)"
                        else -> "5 • Debilitating (Extreme distress / incapacitation)"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SEVERITY LEVEL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${sliderValue.toInt()} OF 5",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = severityAccentColor
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = severityDescription,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = severityAccentColor,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider (1 to 5)
                    Slider(
                        value = sliderValue,
                        onValueChange = {
                            sliderValue = it
                            onSeverityChange(it.toInt().coerceIn(1, 5))
                        },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = severityAccentColor,
                            activeTrackColor = severityAccentColor,
                            inactiveTrackColor = outlineColor,
                            activeTickColor = MaterialTheme.colorScheme.surface,
                            inactiveTickColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_${symptomType.name.lowercase()}")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Precursor Triggers
                    Text(
                        text = "PRECURSOR CONTEXT & TRIGGERS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        symptomType.defaultTriggers.forEach { trigger ->
                            val isSelected = entry?.triggerList?.contains(trigger) == true
                            val chipBg = if (isSelected) symptomType.badgeColor else MaterialTheme.colorScheme.surfaceVariant
                            val chipText = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            val chipBorder = if (isSelected) symptomType.badgeColor else outlineColor

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(chipBg)
                                    .border(1.dp, chipBorder, RoundedCornerShape(12.dp))
                                    .clickable { onTriggerToggle(trigger) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("trigger_${trigger.lowercase().replace(" ", "_")}")
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = trigger,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = chipText
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // User's own notes/words (not generic medical phrasing)
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = {
                            notesText = it
                            onNotesChange(it)
                        },
                        placeholder = {
                            Text(
                                "Describe how this felt in your own words (e.g. snapped at dinner, 3am night chill)...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PlumLight,
                            unfocusedBorderColor = outlineColor,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(14.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("notes_input_${symptomType.name.lowercase()}")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLogged) {
                            TextButton(
                                onClick = onRemoveClick,
                                modifier = Modifier.testTag("remove_btn_${symptomType.name.lowercase()}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove symptom",
                                    tint = BurntOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Clear Entry",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BurntOrange,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Button(
                            onClick = {
                                onSeverityChange(sliderValue.toInt().coerceIn(1, 5))
                                onCardClick()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PlumPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("save_card_btn_${symptomType.name.lowercase()}")
                        ) {
                            Text(
                                "Log Entry",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
