package com.example.data.repository

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Manages GirlyCycle subscription tier and delegates to EntitlementManager and RevenueCatManager.
 */
class SubscriptionManager(context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    val entitlementManager: EntitlementManager = DefaultEntitlementManager.getInstance(context)
    val revenueCatManager: RevenueCatManager = RevenueCatManager.getInstance(context)

    val isPremium: StateFlow<Boolean> = combine(
        entitlementManager.entitlementState,
        revenueCatManager.isRevenueCatSubscribed
    ) { localState, rcSubscribed ->
        localState.isSubscribed || rcSubscribed
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = entitlementManager.entitlementState.value.isSubscribed || revenueCatManager.isRevenueCatSubscribed.value
    )

    fun upgradeToPremium(plan: PremiumPlan = PremiumPlan.ANNUAL) {
        entitlementManager.setSubscribed(true)
    }

    fun downgradeToFreeForTesting() {
        entitlementManager.setSubscribed(false)
    }

    fun restorePurchases(onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        if (revenueCatManager.isConfigured.value) {
            revenueCatManager.restorePurchases { success, message ->
                if (success) {
                    entitlementManager.setSubscribed(true)
                }
                onComplete(success, message)
            }
        } else {
            val restored = entitlementManager.entitlementState.value.isSubscribed
            val msg = if (restored) "Restored offline subscription tier" else "No active subscriptions found"
            onComplete(restored, msg)
        }
    }

    companion object {
        private const val KEY_IS_PREMIUM = "is_premium_unlocked"

        @Volatile
        private var INSTANCE: SubscriptionManager? = null

        fun getInstance(context: Context): SubscriptionManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SubscriptionManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}

enum class PremiumPlan(
    val title: String,
    val price: String,
    val billingPeriod: String,
    val trialTag: String,
    val savingsBadge: String? = null
) {
    ANNUAL(
        title = "Annual Atelier",
        price = "$59.99 / year",
        billingPeriod = "Billed annually ($4.99/mo)",
        trialTag = "7-Day Complimentary Trial",
        savingsBadge = "Save 50%"
    ),
    MONTHLY(
        title = "Monthly Membership",
        price = "$9.99 / month",
        billingPeriod = "Billed monthly, cancel anytime",
        trialTag = "Flexible billing",
        savingsBadge = null
    )
}
