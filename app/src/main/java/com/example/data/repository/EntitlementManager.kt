package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getCustomerInfoWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Entitlement identifiers for GirlyCycle features.
 * Structured to map 1:1 with RevenueCat entitlement identifiers.
 */
enum class AppEntitlement(val entitlementIdentifier: String) {
    DOCTOR_DOSSIER("girlycycle_doctor_dossier"),
    INSIGHTS("girlycycle_insights"),
    ATELIER_MEMBERSHIP("premium")
}

/**
 * Represents the complete entitlement and trial state of the user.
 */
data class EntitlementState(
    val isSubscribed: Boolean = false,
    val isSimulatedPremium: Boolean = false,
    val trialStartDate: LocalDate? = null,
    val daysRemaining: Int = 21,
    val currentTrialDay: Int = 1,
    val isTrialActive: Boolean = true,
    val isTrialExpired: Boolean = false,
    val isDoctorDossierUnlocked: Boolean = false,
    val isInsightsUnlocked: Boolean = false,
    // Daily log and profile are FOREVER FREE, even after trial ends
    val isDailyLogForeverFree: Boolean = true,
    val isProfileForeverFree: Boolean = true,
    // In-app nudges
    val showDay14Nudge: Boolean = false,
    val showDay21ExpiryDialog: Boolean = false
) {
    val trialBadgeText: String
        get() = when {
            isSimulatedPremium -> "Atelier Membership Active (Demo Mode)"
            isSubscribed -> "Atelier Membership Active"
            isTrialActive -> if (daysRemaining == 1) "1 day left in your free trial" else "$daysRemaining days left in your free trial"
            else -> "Free Tier • 21-day trial concluded"
        }
}

/**
 * Central interface for checking feature access and subscription/trial status.
 */
interface EntitlementManager {
    val entitlementState: StateFlow<EntitlementState>

    /**
     * Checks if a specific entitlement is currently active (via RevenueCat "premium" entitlement or subscription).
     */
    fun hasEntitlement(entitlement: AppEntitlement): Boolean

    /**
     * Convenience method for UI screens to check whether a feature is unlocked.
     */
    fun isFeatureUnlocked(entitlement: AppEntitlement): Boolean

    /**
     * Records the user's first app open or first symptom log to start the 21-day countdown.
     */
    fun startTrialIfNeeded(today: LocalDate = LocalDate.now())

    /**
     * Sets or simulates a paid subscription status.
     */
    fun setSubscribed(isSubscribed: Boolean)

    /**
     * Toggles demo / simulation mode for premium access without needing a real RevenueCat purchase.
     */
    fun setSimulatePremium(enabled: Boolean)

    /**
     * Simulates a specific day in the trial for testing/dev preview.
     * E.g. 1 = Day 1 (20 days remaining), 14 = Day 14 (7 days left), 21 = Day 21 (0 days left), 22 = Expired.
     * Set to null to return to real calendar calculation.
     */
    fun simulateTrialDay(dayNumber: Int?)

    /**
     * Resets trial to Day 1 (today) for testing.
     */
    fun resetTrial(today: LocalDate = LocalDate.now())

    /**
     * Dismisses the day 14 insight reminder banner.
     */
    fun dismissDay14Nudge()

    /**
     * Dismisses the day 21 trial expiry dialog.
     */
    fun dismissDay21ExpiryDialog()

    /**
     * Updates entitlement status from RevenueCat CustomerInfo.
     */
    fun updateCustomerInfo(customerInfo: CustomerInfo)

    /**
     * Queries RevenueCat SDK to check CustomerInfo for the "premium" entitlement.
     */
    fun checkRevenueCatEntitlements()
}

/**
 * Implementation that checks RevenueCat CustomerInfo for the "premium" entitlement,
 * replacing the local trial-date logic for feature unlocking while preserving the 21-day trial UI messaging.
 * Supports a "Simulate Premium (Demo Mode)" toggle for demoing unlocked features without a real purchase.
 */
class DefaultEntitlementManager private constructor(
    context: Context
) : EntitlementManager {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var cachedCustomerInfo: CustomerInfo? = null

    private val _entitlementState = MutableStateFlow(calculateCurrentState())
    override val entitlementState: StateFlow<EntitlementState> = _entitlementState.asStateFlow()

    init {
        // Ensure 21-day trial countdown messaging is initialized
        startTrialIfNeeded()
        // Check RevenueCat CustomerInfo for "premium" entitlement
        checkRevenueCatEntitlements()
    }

    override fun updateCustomerInfo(customerInfo: CustomerInfo) {
        cachedCustomerInfo = customerInfo
        val hasPremium = isPremiumActive(customerInfo)
        Log.d(TAG, "RevenueCat CustomerInfo updated: premium entitlement isActive = $hasPremium")
        refreshState(hasRevenueCatPremium = hasPremium)
    }

    override fun checkRevenueCatEntitlements() {
        try {
            if (Purchases.isConfigured) {
                Purchases.sharedInstance.getCustomerInfoWith(
                    onError = { error ->
                        Log.w(TAG, "RevenueCat getCustomerInfo failed: ${error.message}")
                    },
                    onSuccess = { customerInfo ->
                        updateCustomerInfo(customerInfo)
                    }
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Exception querying RevenueCat CustomerInfo", e)
        }
    }

    private fun isPremiumActive(customerInfo: CustomerInfo?): Boolean {
        if (customerInfo == null) return false
        return customerInfo.entitlements["premium"]?.isActive == true ||
               customerInfo.entitlements.active.containsKey("premium")
    }

    override fun hasEntitlement(entitlement: AppEntitlement): Boolean {
        if (prefs.getBoolean(KEY_SIMULATE_PREMIUM, false)) return true

        val rcActive = isPremiumActive(cachedCustomerInfo)
        if (rcActive) return true

        val state = _entitlementState.value
        if (state.isSubscribed) return true

        return when (entitlement) {
            AppEntitlement.DOCTOR_DOSSIER -> state.isDoctorDossierUnlocked
            AppEntitlement.INSIGHTS -> state.isInsightsUnlocked
            AppEntitlement.ATELIER_MEMBERSHIP -> state.isSubscribed
        }
    }

    override fun isFeatureUnlocked(entitlement: AppEntitlement): Boolean {
        return hasEntitlement(entitlement)
    }

    override fun startTrialIfNeeded(today: LocalDate) {
        val existingStart = prefs.getString(KEY_TRIAL_START_DATE, null)
        if (existingStart == null) {
            prefs.edit()
                .putString(KEY_TRIAL_START_DATE, today.format(dateFormatter))
                .apply()
        }
        refreshState(today)
    }

    override fun setSubscribed(isSubscribed: Boolean) {
        prefs.edit().putBoolean(KEY_IS_SUBSCRIBED, isSubscribed).apply()
        refreshState(hasRevenueCatPremium = isSubscribed)
    }

    override fun setSimulatePremium(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SIMULATE_PREMIUM, enabled).apply()
        refreshState()
    }

    override fun simulateTrialDay(dayNumber: Int?) {
        if (dayNumber == null) {
            prefs.edit().remove(KEY_SIMULATED_TRIAL_DAY).apply()
        } else {
            prefs.edit().putInt(KEY_SIMULATED_TRIAL_DAY, dayNumber).apply()
        }
        refreshState()
    }

    override fun resetTrial(today: LocalDate) {
        prefs.edit()
            .putString(KEY_TRIAL_START_DATE, today.format(dateFormatter))
            .putBoolean(KEY_DISMISSED_DAY14_NUDGE, false)
            .putBoolean(KEY_DISMISSED_DAY21_EXPIRY, false)
            .remove(KEY_SIMULATED_TRIAL_DAY)
            .apply()
        refreshState(today)
    }

    override fun dismissDay14Nudge() {
        prefs.edit().putBoolean(KEY_DISMISSED_DAY14_NUDGE, true).apply()
        refreshState()
    }

    override fun dismissDay21ExpiryDialog() {
        prefs.edit().putBoolean(KEY_DISMISSED_DAY21_EXPIRY, true).apply()
        refreshState()
    }

    private fun refreshState(
        today: LocalDate = LocalDate.now(),
        hasRevenueCatPremium: Boolean? = null
    ) {
        _entitlementState.value = calculateCurrentState(today, hasRevenueCatPremium)
    }

    private fun calculateCurrentState(
        today: LocalDate = LocalDate.now(),
        hasRevenueCatPremium: Boolean? = null
    ): EntitlementState {
        val isSimulatedPremium = prefs.getBoolean(KEY_SIMULATE_PREMIUM, false)
        val isRcPremium = hasRevenueCatPremium ?: isPremiumActive(cachedCustomerInfo)
        val isSubscribed = isSimulatedPremium || isRcPremium || prefs.getBoolean(KEY_IS_SUBSCRIBED, false)

        val startDateStr = prefs.getString(KEY_TRIAL_START_DATE, null)
        val startDate = startDateStr?.let {
            runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull()
        } ?: today

        val simulatedDay = if (prefs.contains(KEY_SIMULATED_TRIAL_DAY)) {
            prefs.getInt(KEY_SIMULATED_TRIAL_DAY, 1)
        } else null

        val currentTrialDay: Int
        val daysRemaining: Int

        if (simulatedDay != null) {
            currentTrialDay = simulatedDay
            val elapsed = simulatedDay - 1
            daysRemaining = maxOf(0, TOTAL_TRIAL_DAYS - elapsed)
        } else {
            val daysElapsed = ChronoUnit.DAYS.between(startDate, today).toInt().coerceAtLeast(0)
            currentTrialDay = daysElapsed + 1
            daysRemaining = maxOf(0, TOTAL_TRIAL_DAYS - daysElapsed)
        }

        val isTrialActive = daysRemaining > 0
        val isTrialExpired = daysRemaining <= 0

        // Gated features: Doctor Dossier & Insights are unlocked via RevenueCat "premium" entitlement,
        // or through Demo Mode simulation, replacing the previous local trial-date unlock logic,
        // while preserving the 21-day trial UI messaging and nudges as-is.
        val isDoctorDossierUnlocked = isSubscribed
        val isInsightsUnlocked = isSubscribed

        // In-app nudges for trial messaging
        val dismissedDay14 = prefs.getBoolean(KEY_DISMISSED_DAY14_NUDGE, false)
        val dismissedDay21 = prefs.getBoolean(KEY_DISMISSED_DAY21_EXPIRY, false)

        val showDay14Nudge = !isSubscribed && !dismissedDay14 && currentTrialDay in 14..20 && isTrialActive
        val showDay21ExpiryDialog = !isSubscribed && !dismissedDay21 && isTrialExpired

        return EntitlementState(
            isSubscribed = isSubscribed,
            isSimulatedPremium = isSimulatedPremium,
            trialStartDate = startDate,
            daysRemaining = daysRemaining,
            currentTrialDay = currentTrialDay,
            isTrialActive = isTrialActive,
            isTrialExpired = isTrialExpired,
            isDoctorDossierUnlocked = isDoctorDossierUnlocked,
            isInsightsUnlocked = isInsightsUnlocked,
            isDailyLogForeverFree = true,
            isProfileForeverFree = true,
            showDay14Nudge = showDay14Nudge,
            showDay21ExpiryDialog = showDay21ExpiryDialog
        )
    }

    companion object {
        private const val TAG = "EntitlementManager"
        const val TOTAL_TRIAL_DAYS = 21

        private const val PREFS_NAME = "girlycycle_entitlements_prefs"
        private const val KEY_TRIAL_START_DATE = "trial_start_date"
        private const val KEY_IS_SUBSCRIBED = "is_subscribed"
        private const val KEY_SIMULATED_TRIAL_DAY = "simulated_trial_day"
        private const val KEY_SIMULATE_PREMIUM = "simulate_premium_demo_mode"
        private const val KEY_DISMISSED_DAY14_NUDGE = "dismissed_day14_nudge"
        private const val KEY_DISMISSED_DAY21_EXPIRY = "dismissed_day21_expiry"

        @Volatile
        private var INSTANCE: DefaultEntitlementManager? = null

        fun getInstance(context: Context): DefaultEntitlementManager {
            return INSTANCE ?: synchronized(this) {
                val instance = DefaultEntitlementManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
