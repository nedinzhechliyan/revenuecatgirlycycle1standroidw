package com.example.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Manages RevenueCat Purchases SDK lifecycle, configuration, and entitlement sync.
 */
class RevenueCatManager private constructor(private val appContext: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _isConfigured = MutableStateFlow(false)
    val isConfigured: StateFlow<Boolean> = _isConfigured.asStateFlow()

    private val _isRevenueCatSubscribed = MutableStateFlow(false)
    val isRevenueCatSubscribed: StateFlow<Boolean> = _isRevenueCatSubscribed.asStateFlow()

    private val _customerInfo = MutableStateFlow<CustomerInfo?>(null)
    val customerInfo: StateFlow<CustomerInfo?> = _customerInfo.asStateFlow()

    private val _offerings = MutableStateFlow<Offerings?>(null)
    val offerings: StateFlow<Offerings?> = _offerings.asStateFlow()

    private val _statusMessage = MutableStateFlow("Initializing RevenueCat SDK...")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    val maskedApiKey: String
        get() {
            val key = BuildConfig.REVENUECAT_API_KEY.ifBlank { "test_sDlXVgUSlAErawYsdElMgNTZEOO" }
            if (key.isBlank() || key.contains("PLACEHOLDER")) return "Not Configured"
            return if (key.length > 8) {
                "${key.take(8)}...${key.takeLast(4)}"
            } else {
                key
            }
        }

    init {
        configureSdk()
    }

    fun configureSdk() {
        val apiKey = BuildConfig.REVENUECAT_API_KEY.ifBlank { "test_sDlXVgUSlAErawYsdElMgNTZEOO" }

        try {
            if (!Purchases.isConfigured) {
                Purchases.logLevel = LogLevel.DEBUG
                Purchases.configure(
                    PurchasesConfiguration.Builder(appContext, apiKey)
                        .build()
                )
                Log.i(TAG, "Purchases.configure called successfully with test key: $maskedApiKey")
            }

            _isConfigured.value = true
            _statusMessage.value = "Connected to RevenueCat (SDK v10.23.2)"

            Purchases.sharedInstance.updatedCustomerInfoListener =
                UpdatedCustomerInfoListener { info ->
                    processCustomerInfo(info)
                }

            refreshCustomerInfo()
            fetchOfferings()
        } catch (e: Exception) {
            Log.e(TAG, "Error configuring RevenueCat Purchases SDK", e)
            _statusMessage.value = "RevenueCat configuration note: ${e.localizedMessage ?: "Offline"}"
        }
    }

    fun refreshCustomerInfo(onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        if (!_isConfigured.value) {
            onComplete(false, "RevenueCat not configured")
            return
        }

        try {
            Purchases.sharedInstance.getCustomerInfoWith(
                onError = { error ->
                    Log.w(TAG, "Failed to get customer info: ${error.message}")
                    _statusMessage.value = "Sync note: ${error.message}"
                    onComplete(false, error.message)
                },
                onSuccess = { info ->
                    processCustomerInfo(info)
                    _statusMessage.value = "Customer info synced (${info.entitlements.active.size} active entitlements)"
                    onComplete(true, null)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching customer info", e)
            onComplete(false, e.localizedMessage)
        }
    }

    fun fetchOfferings(onComplete: (Offerings?) -> Unit = {}) {
        if (!_isConfigured.value) {
            onComplete(null)
            return
        }

        try {
            Purchases.sharedInstance.getOfferingsWith(
                onError = { error ->
                    Log.w(TAG, "Error fetching offerings: ${error.message}")
                    onComplete(null)
                },
                onSuccess = { offerings ->
                    _offerings.value = offerings
                    onComplete(offerings)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting offerings", e)
            onComplete(null)
        }
    }

    fun restorePurchases(onComplete: (Boolean, String?) -> Unit) {
        if (!_isConfigured.value) {
            onComplete(false, "RevenueCat not configured")
            return
        }

        try {
            Purchases.sharedInstance.restorePurchasesWith(
                onError = { error ->
                    Log.e(TAG, "Restore purchases failed: ${error.message}")
                    _statusMessage.value = "Restore failed: ${error.message}"
                    onComplete(false, error.message)
                },
                onSuccess = { info ->
                    processCustomerInfo(info)
                    val activeCount = info.entitlements.active.size
                    val msg = if (activeCount > 0) {
                        "Restored $activeCount active entitlement(s)"
                    } else {
                        "No active subscriptions found for this account"
                    }
                    _statusMessage.value = msg
                    onComplete(true, msg)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception during restore", e)
            onComplete(false, e.localizedMessage)
        }
    }

    fun purchasePackage(
        activity: Activity,
        rcPackage: Package,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (!_isConfigured.value) {
            onComplete(false, "RevenueCat not configured")
            return
        }

        try {
            val params = PurchaseParams.Builder(activity, rcPackage).build()
            Purchases.sharedInstance.purchaseWith(
                params,
                onError = { error, userCancelled ->
                    val msg = if (userCancelled) "Purchase cancelled" else error.message
                    Log.w(TAG, "Purchase error: $msg")
                    onComplete(false, msg)
                },
                onSuccess = { storeTransaction, customerInfo ->
                    processCustomerInfo(customerInfo)
                    onComplete(true, "Purchase successful: ${storeTransaction?.orderId ?: "Completed"}")
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Purchase exception", e)
            onComplete(false, e.localizedMessage)
        }
    }

    fun processCustomerInfo(info: CustomerInfo) {
        _customerInfo.value = info
        val hasPremiumEntitlement = info.entitlements["premium"]?.isActive == true ||
                info.entitlements.active.containsKey("premium")

        _isRevenueCatSubscribed.value = hasPremiumEntitlement

        // Sync with EntitlementManager
        DefaultEntitlementManager.getInstance(appContext).updateCustomerInfo(info)
    }

    companion object {
        private const val TAG = "RevenueCatManager"

        @Volatile
        private var INSTANCE: RevenueCatManager? = null

        fun getInstance(context: Context): RevenueCatManager {
            return INSTANCE ?: synchronized(this) {
                val instance = RevenueCatManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
