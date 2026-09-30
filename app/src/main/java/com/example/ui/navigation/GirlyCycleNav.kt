package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.EntitlementState
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.DoctorReportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PaywallScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.TrackScreen
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.EditorialSerif
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.GirlyCycleViewModel

/**
 * 5 Restructured Navigation Tabs:
 * 1. Home (new - replaces landing directly on Daily Log)
 * 2. Track (renamed from Daily Log)
 * 3. Insights (existing, enhanced with cycle length & top 3 trends)
 * 4. Community (new placeholder shell)
 * 5. Profile (renamed from Atelier)
 */
enum class AppNavTab(
    val title: String,
    val icon: ImageVector,
    val requiresPremium: Boolean
) {
    HOME("Home", Icons.Default.Home, false),
    TRACK("Track", Icons.Default.DateRange, false),
    INSIGHTS("Insights", Icons.Default.AutoAwesome, true),
    COMMUNITY("Community", Icons.Default.Groups, false),
    PROFILE("Profile", Icons.Default.Person, false)
}

@Composable
fun GirlyCycleApp(
    viewModel: GirlyCycleViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by rememberSaveable { mutableStateOf(AppNavTab.HOME) }
    var showPaywallModal by rememberSaveable { mutableStateOf(false) }
    var showDoctorDossierScreen by rememberSaveable { mutableStateOf(false) }
    var showOnboardingModal by rememberSaveable { mutableStateOf(false) }

    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userStage by viewModel.userStage.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val loggedDates by viewModel.allLoggedDates.collectAsStateWithLifecycle()
    val streakCount by viewModel.streakCount.collectAsStateWithLifecycle()
    val daySymptoms by viewModel.daySymptomMap.collectAsStateWithLifecycle()
    val currentDailyNote by viewModel.currentDayNote.collectAsStateWithLifecycle()
    val allEntries by viewModel.allEntries.collectAsStateWithLifecycle()
    val topFrequentSymptoms by viewModel.topFrequentSymptoms.collectAsStateWithLifecycle()
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val entitlementState by viewModel.entitlementState.collectAsStateWithLifecycle()
    val doctorReport by viewModel.doctorReport.collectAsStateWithLifecycle()
    val insights by viewModel.insights.collectAsStateWithLifecycle()
    val saveFeedback by viewModel.saveFeedback.collectAsStateWithLifecycle()

    val isLargeText by viewModel.isLargeText.collectAsStateWithLifecycle()
    val isHighContrast by viewModel.isHighContrast.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val revenueCatStatus by viewModel.subscriptionManager.revenueCatManager.statusMessage.collectAsStateWithLifecycle()
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (!isOnboardingCompleted || showOnboardingModal) {
            OnboardingScreen(
                initialName = userName,
                onCompleteOnboarding = { newName, newStage ->
                    viewModel.setUserName(newName)
                    viewModel.setUserStage(newStage)
                    viewModel.setOnboardingCompleted(true)
                    showOnboardingModal = false
                }
            )
        } else if (showDoctorDossierScreen) {
            // Full Screen Doctor Dossier accessed via Insights
            DoctorReportScreen(
                report = doctorReport,
                entitlementState = entitlementState,
                onShareReport = { ctx -> viewModel.shareDoctorReport(ctx) },
                onOpenPaywall = { showPaywallModal = true },
                onBack = { showDoctorDossierScreen = false }
            )
        } else {
            Scaffold(
                bottomBar = {
                    GirlyCycleBottomBar(
                        currentTab = currentTab,
                        entitlementState = entitlementState,
                        onTabSelected = { tab ->
                            currentTab = tab
                        }
                    )
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (currentTab) {
                        AppNavTab.HOME -> {
                            HomeScreen(
                                userName = userName,
                                userStage = userStage,
                                recentEntries = allEntries,
                                currentDailyNote = currentDailyNote,
                                streakCount = streakCount,
                                onQuickLogPeriod = { flow -> viewModel.quickLogPeriod(flow) },
                                onQuickLogSymptom = { sym, sev -> viewModel.quickLogSymptom(sym, sev) },
                                onQuickLogSleep = { hadPoorSleep -> viewModel.quickLogSleep(hadPoorSleep) },
                                onQuickLogNote = { note -> viewModel.quickLogNote(note) },
                                onNavigateToTrack = { currentTab = AppNavTab.TRACK },
                                onNavigateToInsights = { currentTab = AppNavTab.INSIGHTS }
                            )
                        }

                        AppNavTab.TRACK -> {
                            TrackScreen(
                                userName = userName,
                                userStage = userStage,
                                selectedDate = selectedDate,
                                loggedDates = loggedDates,
                                streakCount = streakCount,
                                daySymptoms = daySymptoms,
                                currentDailyNote = currentDailyNote,
                                topFrequentSymptoms = topFrequentSymptoms,
                                isPremium = isPremium,
                                saveFeedback = saveFeedback,
                                onDateSelected = { viewModel.selectDate(it) },
                                onSelectToday = { viewModel.selectToday() },
                                onSelectYesterday = { viewModel.selectYesterday() },
                                onSeverityChange = { type, sev -> viewModel.setSymptomSeverity(type, sev) },
                                onNotesChange = { type, notes -> viewModel.updateSymptomNotes(type, notes) },
                                onTriggerToggle = { type, trig -> viewModel.toggleTrigger(type, trig) },
                                onRemoveSymptom = { type -> viewModel.toggleSymptom(type) },
                                onDailyNoteUpdate = { flow, energy, reflection ->
                                    viewModel.updateDailyNote(flow, energy, reflection)
                                },
                                onUpdateName = { viewModel.setUserName(it) },
                                onOpenPaywall = { showPaywallModal = true }
                            )
                        }

                        AppNavTab.INSIGHTS -> {
                            InsightsScreen(
                                insights = insights,
                                entitlementState = entitlementState,
                                onOpenPaywall = { showPaywallModal = true },
                                onOpenDoctorDossier = { showDoctorDossierScreen = true }
                            )
                        }

                        AppNavTab.COMMUNITY -> {
                            CommunityScreen()
                        }

                        AppNavTab.PROFILE -> {
                            ProfileSettingsScreen(
                                userName = userName,
                                userStage = userStage,
                                entitlementState = entitlementState,
                                totalLoggedDays = loggedDates.size,
                                isLargeText = isLargeText,
                                isHighContrast = isHighContrast,
                                isDarkMode = isDarkMode,
                                onToggleLargeText = { viewModel.toggleLargeText(it) },
                                onToggleHighContrast = { viewModel.toggleHighContrast(it) },
                                onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                                onRerunOnboarding = { showOnboardingModal = true },
                                onUpdateName = { viewModel.setUserName(it) },
                                onUpdateStage = { viewModel.setUserStage(it) },
                                onUpgrade = { viewModel.upgradeToPremium() },
                                onDowngradeForTesting = { viewModel.downgradeToFree() },
                                onSimulateTrialDay = { viewModel.simulateTrialDay(it) },
                                onResetTrial = { viewModel.resetTrial() },
                                onToggleSimulatePremium = { viewModel.toggleSimulatePremium(it) },
                                onOpenPaywall = { showPaywallModal = true },
                                revenueCatStatus = revenueCatStatus,
                                revenueCatMaskedKey = viewModel.subscriptionManager.revenueCatManager.maskedApiKey,
                                onRestorePurchases = { viewModel.restorePurchases() },
                                onSyncRevenueCat = { viewModel.syncRevenueCat() }
                            )
                        }
                    }
                }
            }
        }

        // In-App Nudge Dialog: Day 14 Pattern Insight Available
        if (entitlementState.showDay14Nudge) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDay14Nudge() },
                title = {
                    Text(
                        text = "14 Days of Rhythm Tracked",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    Text(
                        text = "You've logged enough for your first pattern insight — check your Insights tab.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.dismissDay14Nudge()
                            currentTab = AppNavTab.INSIGHTS
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Check Insights Tab")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDay14Nudge() }) {
                        Text("Dismiss")
                    }
                }
            )
        }

        // In-App Nudge Dialog: Day 21 Trial Expiry
        if (entitlementState.showDay21ExpiryDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDay21ExpiryDialog() },
                title = {
                    Text(
                        text = "Your Free Trial Has Ended",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    Text(
                        text = "Your free trial has ended. Upgrade to keep your Doctor Dossier and Insights unlocked.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.dismissDay21ExpiryDialog()
                            showPaywallModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Upgrade to Atelier")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDay21ExpiryDialog() }) {
                        Text("Continue Free Logging")
                    }
                }
            )
        }

        // Full Screen Paywall Modal
        AnimatedVisibility(
            visible = showPaywallModal,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.fillMaxSize()
        ) {
            PaywallScreen(
                isPremium = isPremium,
                isSimulatedPremium = entitlementState.isSimulatedPremium,
                onUpgrade = {
                    viewModel.upgradeToPremium()
                    showPaywallModal = false
                },
                onDowngradeForTesting = {
                    viewModel.downgradeToFree()
                },
                onToggleSimulatePremium = {
                    viewModel.toggleSimulatePremium(it)
                },
                onRestorePurchases = {
                    viewModel.restorePurchases()
                },
                revenueCatManager = viewModel.subscriptionManager.revenueCatManager,
                onDismiss = { showPaywallModal = false }
            )
        }
    }
}

@Composable
fun GirlyCycleBottomBar(
    currentTab: AppNavTab,
    entitlementState: EntitlementState,
    onTabSelected: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val outlineColor = MaterialTheme.colorScheme.outline

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("girlycycle_bottom_nav"),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppNavTab.entries.forEach { tab ->
                val isSelected = currentTab == tab
                val isLocked = when (tab) {
                    AppNavTab.INSIGHTS -> !entitlementState.isInsightsUnlocked
                    AppNavTab.HOME, AppNavTab.TRACK, AppNavTab.COMMUNITY, AppNavTab.PROFILE -> false
                }

                val activeBg = if (isSelected) TealPrimary.copy(alpha = 0.12f) else Color.Transparent
                val activeBorder = if (isSelected) TealLight.copy(alpha = 0.4f) else Color.Transparent

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(activeBg)
                        .border(1.dp, activeBorder, RoundedCornerShape(14.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("nav_tab_${tab.name.lowercase()}")
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        if (isLocked) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(CoralPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}
