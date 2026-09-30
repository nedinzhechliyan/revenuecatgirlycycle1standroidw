package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.EditorialSerif
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    initialName: String = "Helen",
    onCompleteOnboarding: (name: String, stage: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 6 // Welcome, Stage & Age, Goals, Symptoms, Privacy, Preview

    // State collected across steps
    var enteredName by remember { mutableStateOf(initialName) }
    var selectedAgeGroup by remember { mutableStateOf("45-49") }
    var selectedStageQuestion by remember { mutableStateOf("Periods have become irregular or unpredictable") }
    val selectedGoals = remember { mutableStateOf(setOf("Track sleep & night wakings", "Prepare evidence for my doctor")) }
    val selectedSymptoms = remember { mutableStateOf(setOf("Fatigue", "Poor Sleep", "Brain Fog", "Mood Swings / Rage")) }
    var optInAnonymizedResearch by remember { mutableStateOf(false) }

    val computedStageString = remember(selectedStageQuestion, selectedAgeGroup) {
        when {
            selectedStageQuestion.contains("irregular", ignoreCase = true) -> "Perimenopause • Irregular Rhythm"
            selectedStageQuestion.contains("stopped", ignoreCase = true) -> "Menopause Transition"
            selectedStageQuestion.contains("regular", ignoreCase = true) -> "Early Perimenopause • Subtle Shifts"
            else -> "Perimenopause • Year 2-3"
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("onboarding_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top Nav & Progress Indicator (Steps 1 to 5)
            if (step > 0 && step < totalSteps - 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (step > 0) step-- },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    LinearProgressIndicator(
                        progress = { (step.toFloat()) / (totalSteps - 1).toFloat() },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = TealPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    TextButton(
                        onClick = { step = totalSteps - 1 },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Animated Screen Step Body
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                        }
                    },
                    label = "onboarding_step_anim"
                ) { targetStep ->
                    when (targetStep) {
                        // --------------------------------------------------------
                        // STEP 0: WELCOME SCREEN
                        // --------------------------------------------------------
                        0 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(TealPrimary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Spa,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "GIRLYCYCLE",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = CoralPrimary,
                                        letterSpacing = 2.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Your Body Isn't Broken.\nIt's Transitioning.",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontFamily = EditorialSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        lineHeight = 32.sp
                                    ),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "A quiet, clinically grounded rhythm companion for Gen-X women. No toxic positivity, no dismissed sensations — just clear evidence and validating patterns.",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 24.sp
                                    ),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                OutlinedTextField(
                                    value = enteredName,
                                    onValueChange = { enteredName = it },
                                    label = { Text("What should we call you?") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("onboarding_input_name"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = TealPrimary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = { step = 1 },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("onboarding_get_started_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text(
                                        text = "Get Started",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        // --------------------------------------------------------
                        // STEP 1: AGE & STAGE SELECTOR (Questions, not just age)
                        // --------------------------------------------------------
                        1 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Where are you in your rhythm?",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = EditorialSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Age is just one marker. How your periods and hormonal signals feel matters most.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "AGE GROUP",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TealPrimary,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("Under 45", "45–49", "50–54", "55+").forEach { ageGroup ->
                                        val isSelected = selectedAgeGroup == ageGroup
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) TealPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                                .clickable { selectedAgeGroup = ageGroup }
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = ageGroup,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "WHICH BEST DESCRIBES YOUR BLEEDING PATTERN?",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TealPrimary,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                listOf(
                                    "Still regular, but feeling new sleep or mood fluctuations",
                                    "Periods have become irregular or unpredictable",
                                    "Cycles are skipping 2 to 3 months at a time",
                                    "Periods have completely stopped for 12+ months",
                                    "Had a hysterectomy or taking hormone therapy"
                                ).forEach { question ->
                                    val isSelected = selectedStageQuestion == question
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 5.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { selectedStageQuestion = question },
                                        color = if (isSelected) TealPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(
                                            if (isSelected) 1.5.dp else 1.dp,
                                            if (isSelected) TealPrimary else MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Check,
                                                contentDescription = null,
                                                tint = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                text = question,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = { step = 2 },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Next: Your Goals", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // --------------------------------------------------------
                        // STEP 2: GOALS SCREEN (Pick up to 3)
                        // --------------------------------------------------------
                        2 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "What matters most to you right now?",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = EditorialSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Select up to 3 goals so we can highlight relevant insights for you.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                val goalOptions = listOf(
                                    "Track sleep & night wakings",
                                    "Prepare evidence for my doctor",
                                    "Understand brain fog & mental focus",
                                    "Manage sudden mood shifts & rage",
                                    "Monitor temperature spikes & hot flashes",
                                    "Track irregular menstrual flow",
                                    "Find non-judgmental community validation"
                                )

                                goalOptions.forEach { goal ->
                                    val isSelected = selectedGoals.value.contains(goal)
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 5.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                val current = selectedGoals.value.toMutableSet()
                                                if (isSelected) {
                                                    current.remove(goal)
                                                } else if (current.size < 3) {
                                                    current.add(goal)
                                                }
                                                selectedGoals.value = current
                                            },
                                        color = if (isSelected) CoralPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(
                                            if (isSelected) 1.5.dp else 1.dp,
                                            if (isSelected) CoralPrimary else MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Check,
                                                contentDescription = null,
                                                tint = if (isSelected) CoralPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                text = goal,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = { step = 3 },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Next: Common Sensations", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // --------------------------------------------------------
                        // STEP 3: SYMPTOM QUICK-PICK (8-12 Multi-select)
                        // --------------------------------------------------------
                        3 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Which sensations have you felt recently?",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = EditorialSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Select any that resonate. We will pin these to your daily Track bar.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                val symptomsList = listOf(
                                    "Fatigue",
                                    "Poor Sleep",
                                    "Brain Fog",
                                    "Irritability",
                                    "Mood Swings / Rage",
                                    "Hot Flashes",
                                    "Night Sweats",
                                    "Cycle Shift",
                                    "Joint Aches",
                                    "Anxiety & Tension",
                                    "Bloating & Digestion",
                                    "Intimacy / Libido Shifts"
                                )

                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    symptomsList.forEach { sym ->
                                        val isSelected = selectedSymptoms.value.contains(sym)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSelected) TealPrimary else MaterialTheme.colorScheme.surface)
                                                .border(
                                                    1.dp,
                                                    if (isSelected) TealPrimary else MaterialTheme.colorScheme.outline,
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .clickable {
                                                    val cur = selectedSymptoms.value.toMutableSet()
                                                    if (isSelected) cur.remove(sym) else cur.add(sym)
                                                    selectedSymptoms.value = cur
                                                }
                                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                        ) {
                                            Text(
                                                text = if (isSelected) "✓ $sym" else "+ $sym",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(28.dp))

                                Button(
                                    onClick = { step = 4 },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Next: Privacy Promise", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // --------------------------------------------------------
                        // STEP 4: PRIVACY SCREEN (Plain Language + Opt-in)
                        // --------------------------------------------------------
                        4 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(TealPrimary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = TealPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Text(
                                        text = "Your Privacy is Sovereign",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontFamily = EditorialSerif,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "Plain-Language Guarantee:",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = "• All your daily logs, notes, and cycle data stay stored safely on this device.\n• We will never sell your personal health records to advertisers or third-party data brokers.\n• You can export your clinical Doctor Dossier or delete your data at any time.",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 22.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Anonymized Women's Health Research",
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                            Text(
                                                text = "Optional: contribute de-identified pattern metrics to advance perimenopause research.",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                        Switch(
                                            checked = optInAnonymizedResearch,
                                            onCheckedChange = { optInAnonymizedResearch = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = TealPrimary
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(28.dp))

                                Button(
                                    onClick = { step = 5 },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Preview My Dashboard", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // --------------------------------------------------------
                        // STEP 5: HOME SCREEN PREVIEW & FINISH
                        // --------------------------------------------------------
                        5 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(CoralPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = CoralPrimary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Ready to Begin, $enteredName",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontFamily = EditorialSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    ),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Here is a preview of your personalized rhythm tracker:",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // Mini Preview Card of Home Dashboard
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "TODAY'S STATUS PREVIEW",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TealPrimary,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Your 21-Day Free Trial is Active. Logging unlocked indefinitely.",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = EditorialSerif,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Pinned Sensations: ${selectedSymptoms.value.take(4).joinToString(", ")}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(30.dp))

                                Button(
                                    onClick = {
                                        onCompleteOnboarding(enteredName, computedStageString)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("onboarding_finish_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text(
                                        text = "Open GirlyCycle Home",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
