package com.example

import android.app.Application
import android.util.Log
import com.example.data.repository.DefaultEntitlementManager
import com.example.data.repository.RevenueCatManager
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            // Configure RevenueCat Purchases SDK using the test API key
            Purchases.logLevel = LogLevel.DEBUG
            Purchases.configure(
                PurchasesConfiguration.Builder(this, "test_sDlXVgUSlAErawYsdElMgNTZEOO")
                    .build()
            )
            Log.i("MainApplication", "RevenueCat Purchases SDK configured successfully")

            // Initialize EntitlementManager & RevenueCatManager singletons
            val entitlementManager = DefaultEntitlementManager.getInstance(this)
            val revenueCatManager = RevenueCatManager.getInstance(this)

            // Register updated customer info listener to update EntitlementManager
            Purchases.sharedInstance.updatedCustomerInfoListener =
                UpdatedCustomerInfoListener { customerInfo ->
                    entitlementManager.updateCustomerInfo(customerInfo)
                    revenueCatManager.processCustomerInfo(customerInfo)
                }

            // Immediately check RevenueCat CustomerInfo
            entitlementManager.checkRevenueCatEntitlements()
        } catch (e: Exception) {
            Log.e("MainApplication", "Error during MainApplication initialization", e)
        }
    }
}
