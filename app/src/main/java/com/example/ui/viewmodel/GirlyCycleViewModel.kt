package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DailyNote
import com.example.data.model.SymptomCategory
import com.example.data.model.SymptomEntry
import com.example.data.model.SymptomType
import com.example.data.repository.DefaultEntitlementManager
import com.example.data.repository.EntitlementManager
import com.example.data.repository.EntitlementState
import com.example.data.repository.GirlyCycleRepository
import com.example.data.repository.SubscriptionManager
import com.example.domain.DoctorReport
import com.example.domain.DoctorReportGenerator
import com.example.domain.InsightItem
import com.example.domain.RuleBasedInsightsEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GirlyCycleViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = GirlyCycleRepository(database.symptomDao())
    val subscriptionManager = SubscriptionManager.getInstance(application)
    val entitlementManager: EntitlementManager = DefaultEntitlementManager.getInstance(application)
    val entitlementState: StateFlow<EntitlementState> = entitlementManager.entitlementState

    private val prefs = application.getSharedPreferences("girlycycle_settings", Context.MODE_PRIVATE)
    private val profilePrefs = application.getSharedPreferences("girlycycle_profile", Context.MODE_PRIVATE)

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _userName = MutableStateFlow(
        profilePrefs.getString("user_name", "Helen") ?: "Helen"
    )
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userStage = MutableStateFlow(
        profilePrefs.getString("user_stage", "Perimenopause • Year 3") ?: "Perimenopause • Year 3"
    )
    val userStage: StateFlow<String> = _userStage.asStateFlow()

    // Accessibility & Design Settings
    private val _isLargeText = MutableStateFlow(prefs.getBoolean("large_text", false))
    val isLargeText: StateFlow<Boolean> = _isLargeText.asStateFlow()

    private val _isHighContrast = MutableStateFlow(prefs.getBoolean("high_contrast", false))
    val isHighContrast: StateFlow<Boolean> = _isHighContrast.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Onboarding completion flag
    private val _isOnboardingCompleted = MutableStateFlow(
        prefs.getBoolean("onboarding_completed", true) // Default true for existing users, can be re-run from Profile
    )
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _selectedCategory = MutableStateFlow<SymptomCategory?>(null)
    val selectedCategory: StateFlow<SymptomCategory?> = _selectedCategory.asStateFlow()

    private val _saveFeedback = MutableStateFlow<String?>(null)
    val saveFeedback: StateFlow<String?> = _saveFeedback.asStateFlow()

    val isPremium: StateFlow<Boolean> = subscriptionManager.isPremium

    val allLoggedDates: StateFlow<Set<String>> = repository.distinctLoggedDates
        .combine(MutableStateFlow(Unit)) { dates, _ -> dates.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val daySymptomMap: StateFlow<Map<SymptomType, SymptomEntry>> = _selectedDate
        .flatMapLatest { date ->
            val dateStr = date.format(dateFormatter)
            repository.getEntriesForDate(dateStr)
        }
        .combine(MutableStateFlow(Unit)) { entries, _ ->
            entries.associateBy { it.symptomType }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentDayNote: StateFlow<DailyNote?> = _selectedDate
        .flatMapLatest { date ->
            val dateStr = date.format(dateFormatter)
            repository.getDailyNote(dateStr)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allEntries: StateFlow<List<SymptomEntry>> = repository.allEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Smart Defaults: 5 most commonly logged symptoms
    val topFrequentSymptoms: StateFlow<List<SymptomType>> = allEntries.map { entries ->
        if (entries.isEmpty()) {
            listOf(
                SymptomType.FATIGUE,
                SymptomType.IRRITABILITY,
                SymptomType.POOR_SLEEP,
                SymptomType.BRAIN_FOG,
                SymptomType.MOOD_SWINGS_RAGE
            )
        } else {
            val frequencyMap = entries.groupBy { it.symptomType }
                .mapValues { it.value.size }
            val sorted = frequencyMap.entries.sortedByDescending { it.value }.map { it.key }
            val fallback = listOf(
                SymptomType.FATIGUE,
                SymptomType.IRRITABILITY,
                SymptomType.POOR_SLEEP,
                SymptomType.BRAIN_FOG,
                SymptomType.MOOD_SWINGS_RAGE
            )
            (sorted + fallback).distinct().take(5)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(
            SymptomType.FATIGUE,
            SymptomType.IRRITABILITY,
            SymptomType.POOR_SLEEP,
            SymptomType.BRAIN_FOG,
            SymptomType.MOOD_SWINGS_RAGE
        )
    )

    val doctorReport: StateFlow<DoctorReport> = combine(
        allEntries,
        _userName,
        _userStage
    ) { entries, name, stage ->
        DoctorReportGenerator.generateReport(
            patientName = name,
            userStage = stage,
            endDate = LocalDate.now(),
            windowDays = 30,
            entries = entries,
            dailyNotes = emptyList()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DoctorReportGenerator.generateReport("Helen", "Perimenopause", LocalDate.now(), 30, emptyList(), emptyList())
    )

    val insights: StateFlow<List<InsightItem>> = allEntries
        .combine(MutableStateFlow(emptyList<DailyNote>())) { entries, notes ->
            RuleBasedInsightsEngine.generateInsights(entries, notes)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val streakCount: StateFlow<Int> = allLoggedDates.combine(MutableStateFlow(Unit)) { dates, _ ->
        calculateStreak(dates)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init {
        viewModelScope.launch {
            repository.seedSampleHistoryIfEmpty()
        }
    }

    private fun calculateStreak(loggedDates: Set<String>): Int {
        var streak = 0
        var current = LocalDate.now()
        val todayStr = current.format(dateFormatter)
        if (!loggedDates.contains(todayStr)) {
            current = current.minusDays(1)
        }
        while (loggedDates.contains(current.format(dateFormatter))) {
            streak++
            current = current.minusDays(1)
        }
        return streak
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun selectToday() {
        _selectedDate.value = LocalDate.now()
    }

    fun selectYesterday() {
        _selectedDate.value = LocalDate.now().minusDays(1)
    }

    fun selectCategory(category: SymptomCategory?) {
        _selectedCategory.value = category
    }

    fun setSymptomSeverity(type: SymptomType, severity: Int) {
        viewModelScope.launch {
            entitlementManager.startTrialIfNeeded()
            val dateStr = _selectedDate.value.format(dateFormatter)
            val existing = daySymptomMap.value[type]
            repository.saveSymptom(
                date = dateStr,
                symptomType = type,
                severity = severity,
                notes = existing?.notes ?: "",
                triggers = existing?.triggers ?: "",
                count = existing?.count ?: 1
            )
            showSaveFeedback("Logged ${type.displayName} (Severity $severity/5)")
        }
    }

    fun toggleSymptom(type: SymptomType) {
        viewModelScope.launch {
            val existing = daySymptomMap.value[type]
            val dateStr = _selectedDate.value.format(dateFormatter)
            if (existing != null) {
                repository.removeSymptom(dateStr, type)
                showSaveFeedback("Cleared ${type.displayName}")
            } else {
                repository.saveSymptom(
                    date = dateStr,
                    symptomType = type,
                    severity = 3
                )
                showSaveFeedback("Logged ${type.displayName}")
            }
        }
    }

    fun updateSymptomNotes(type: SymptomType, notes: String) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(dateFormatter)
            val existing = daySymptomMap.value[type]
            if (existing != null) {
                repository.saveSymptom(
                    date = dateStr,
                    symptomType = type,
                    severity = existing.severity,
                    notes = notes,
                    triggers = existing.triggers,
                    count = existing.count
                )
            } else {
                repository.saveSymptom(
                    date = dateStr,
                    symptomType = type,
                    severity = 3,
                    notes = notes
                )
            }
        }
    }

    fun toggleTrigger(type: SymptomType, trigger: String) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(dateFormatter)
            val existing = daySymptomMap.value[type]
            val currentTriggers = existing?.triggerList?.toMutableList() ?: mutableListOf()
            if (currentTriggers.contains(trigger)) {
                currentTriggers.remove(trigger)
            } else {
                currentTriggers.add(trigger)
            }
            val triggerString = currentTriggers.joinToString(",")
            val severity = existing?.severity ?: 3

            repository.saveSymptom(
                date = dateStr,
                symptomType = type,
                severity = severity,
                notes = existing?.notes ?: "",
                triggers = triggerString,
                count = existing?.count ?: 1
            )
        }
    }

    fun updateDailyNote(flow: String, energyScore: Int, reflection: String) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(dateFormatter)
            repository.saveDailyNote(
                date = dateStr,
                flow = flow,
                energyScore = energyScore,
                reflection = reflection
            )
            showSaveFeedback("Day reflection saved")
        }
    }

    // Quick-Log functions for Home screen 1-tap logging
    fun quickLogPeriod(flow: String) {
        viewModelScope.launch {
            val dateStr = LocalDate.now().format(dateFormatter)
            val existing = currentDayNote.value
            repository.saveDailyNote(
                date = dateStr,
                flow = flow,
                energyScore = existing?.energyScore ?: 3,
                reflection = existing?.generalReflection ?: ""
            )
            showSaveFeedback("Flow logged: $flow")
        }
    }

    fun quickLogSymptom(type: SymptomType, severity: Int = 3) {
        viewModelScope.launch {
            entitlementManager.startTrialIfNeeded()
            val dateStr = LocalDate.now().format(dateFormatter)
            repository.saveSymptom(
                date = dateStr,
                symptomType = type,
                severity = severity
            )
            showSaveFeedback("Logged ${type.displayName}")
        }
    }

    fun quickLogSleep(hadPoorSleep: Boolean) {
        viewModelScope.launch {
            val dateStr = LocalDate.now().format(dateFormatter)
            if (hadPoorSleep) {
                repository.saveSymptom(
                    date = dateStr,
                    symptomType = SymptomType.POOR_SLEEP,
                    severity = 4
                )
                showSaveFeedback("Logged Fragmented Sleep")
            } else {
                repository.removeSymptom(dateStr, SymptomType.POOR_SLEEP)
                showSaveFeedback("Sleep logged as Restful")
            }
        }
    }

    fun quickLogNote(noteText: String) {
        viewModelScope.launch {
            val dateStr = LocalDate.now().format(dateFormatter)
            val existing = currentDayNote.value
            repository.saveDailyNote(
                date = dateStr,
                flow = existing?.menstrualFlow ?: "None",
                energyScore = existing?.energyScore ?: 3,
                reflection = noteText
            )
            showSaveFeedback("Daily note saved")
        }
    }

    // Settings & Accessibility Toggles
    fun toggleLargeText(enabled: Boolean) {
        _isLargeText.value = enabled
        prefs.edit().putBoolean("large_text", enabled).apply()
    }

    fun toggleHighContrast(enabled: Boolean) {
        _isHighContrast.value = enabled
        prefs.edit().putBoolean("high_contrast", enabled).apply()
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean("dark_mode", enabled).apply()
    }

    fun setOnboardingCompleted(completed: Boolean) {
        _isOnboardingCompleted.value = completed
        prefs.edit().putBoolean("onboarding_completed", completed).apply()
    }

    fun setUserName(name: String) {
        val trimmed = name.trim().ifBlank { "Helen" }
        _userName.value = trimmed
        profilePrefs.edit().putString("user_name", trimmed).apply()
    }

    fun setUserStage(stage: String) {
        val trimmed = stage.trim().ifBlank { "Perimenopause • Year 3" }
        _userStage.value = trimmed
        profilePrefs.edit().putString("user_stage", trimmed).apply()
    }

    fun upgradeToPremium() {
        entitlementManager.setSubscribed(true)
        showSaveFeedback("Unlocked GirlyCycle Atelier Premium")
    }

    fun downgradeToFree() {
        entitlementManager.setSubscribed(false)
        entitlementManager.setSimulatePremium(false)
        showSaveFeedback("Switched to Free Tier (Preview Mode)")
    }

    fun toggleSimulatePremium(enabled: Boolean) {
        entitlementManager.setSimulatePremium(enabled)
        val msg = if (enabled) {
            "Simulate Premium (Demo Mode) Enabled: Unlocked Doctor Dossier & Insights"
        } else {
            "Simulate Premium (Demo Mode) Disabled: Reverted to standard tier"
        }
        showSaveFeedback(msg)
    }

    fun restorePurchases() {
        subscriptionManager.restorePurchases { success, msg ->
            showSaveFeedback(msg ?: if (success) "Restored active subscription" else "No purchases to restore")
        }
    }

    fun syncRevenueCat() {
        subscriptionManager.revenueCatManager.refreshCustomerInfo { success, error ->
            val msg = if (success) "Synced with RevenueCat" else "Sync status: ${error ?: "Offline"}"
            showSaveFeedback(msg)
        }
    }

    fun simulateTrialDay(day: Int?) {
        entitlementManager.simulateTrialDay(day)
        val msg = if (day != null) "Simulating Trial Day $day of 21" else "Reset to Real Device Calendar"
        showSaveFeedback(msg)
    }

    fun resetTrial() {
        entitlementManager.resetTrial()
        showSaveFeedback("Reset trial to Day 1 (21 days remaining)")
    }

    fun dismissDay14Nudge() {
        entitlementManager.dismissDay14Nudge()
    }

    fun dismissDay21ExpiryDialog() {
        entitlementManager.dismissDay21ExpiryDialog()
    }

    fun shareDoctorReport(context: Context) {
        val report = doctorReport.value
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "GirlyCycle Clinical Report – ${report.patientName} (${report.dateRangeDisplay})")
            putExtra(Intent.EXTRA_TEXT, report.formattedShareableSummary)
        }
        val chooser = Intent.createChooser(intent, "Share Clinical Report via...")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun showSaveFeedback(message: String) {
        _saveFeedback.value = message
    }

    fun clearSaveFeedback() {
        _saveFeedback.value = null
    }
}
