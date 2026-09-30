package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PremiumPlan
import com.example.data.repository.RevenueCatManager
import com.example.ui.theme.BurntOrange
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.EditorialSerif
import com.example.ui.theme.Mustard
import com.example.ui.theme.PlumLight
import com.example.ui.theme.PlumPrimary
import com.example.ui.theme.Sage
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchaseWith
import kotlinx.coroutines.launch

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun PaywallScreen(
    isPremium: Boolean,
    isSimulatedPremium: Boolean = false,
    onUpgrade: () -> Unit,
    onDowngradeForTesting: () -> Unit,
    onToggleSimulatePremium: ((Boolean) -> Unit)? = null,
    onDismiss: () -> Unit,
    onRestorePurchases: (() -> Unit)? = null,
    revenueCatManager: RevenueCatManager? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val outlineColor = MaterialTheme.colorScheme.outline

    // RevenueCat Offerings State
    var offerings by remember { mutableStateOf<Offerings?>(revenueCatManager?.offerings?.value) }
    var isLoadingOfferings by remember { mutableStateOf(true) }
    var selectedRcPackage by remember { mutableStateOf<Package?>(null) }
    var fallbackSelectedPlan by remember { mutableStateOf(PremiumPlan.ANNUAL) }
    var isPurchasing by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Fetch Offerings on open using Purchases.sharedInstance.getOfferings
    LaunchedEffect(Unit) {
        try {
            if (Purchases.isConfigured) {
                Purchases.sharedInstance.getOfferingsWith(
                    onError = { error ->
                        Log.w("PaywallScreen", "RevenueCat getOfferings error: ${error.message}")
                        isLoadingOfferings = false
                    },
                    onSuccess = { fetchedOfferings ->
                        offerings = fetchedOfferings
                        val pkgs = fetchedOfferings.current?.availablePackages.orEmpty()
                        selectedRcPackage = pkgs.firstOrNull()
                        isLoadingOfferings = false
                    }
                )
            } else {
                revenueCatManager?.fetchOfferings { fetched ->
                    offerings = fetched
                    selectedRcPackage = fetched?.current?.availablePackages?.firstOrNull()
                    isLoadingOfferings = false
                } ?: run {
                    isLoadingOfferings = false
                }
            }
        } catch (e: Exception) {
            Log.e("PaywallScreen", "Exception fetching offerings", e)
            isLoadingOfferings = false
        }
    }

    val availablePackages = offerings?.current?.availablePackages.orEmpty()
    val hasLiveOfferings = availablePackages.isNotEmpty()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("paywall_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dismiss button row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Mustard,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GIRLYCYCLE ATELIER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Mustard
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("paywall_close_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close paywall",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 21-Day Free Trial Hero Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = TealPrimary.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.4f)),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "21-DAY COMPLIMENTARY TRIAL INCLUDED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            // Editorial Headline
            Text(
                text = "Reclaim your body's narrative.",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = EditorialSerif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Unlock clinical doctor dossiers and correlation insights built specifically for the perimenopausal transformation. Free for 21 days, cancel anytime.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Loading Indicator or Plan Selection
            if (isLoadingOfferings) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = TealPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Fetching Atelier Offerings from RevenueCat...",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            } else if (hasLiveOfferings) {
                // RevenueCat Live Offerings Cards
                Text(
                    text = "SELECT AN ATELIER PLAN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    availablePackages.forEach { pkg ->
                        val isSelected = selectedRcPackage == pkg
                        val formattedPrice = pkg.product.price.formatted
                        val packageTitle = when {
                            pkg.identifier.contains("annual", ignoreCase = true) -> "Annual Atelier Plan"
                            pkg.identifier.contains("month", ignoreCase = true) -> "Monthly Membership"
                            else -> pkg.product.title.ifBlank { pkg.identifier }
                        }
                        val subtitleText = "21 days free • then $formattedPrice"

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedRcPackage = pkg }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) TealPrimary else outlineColor.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            color = if (isSelected) TealPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = packageTitle,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = subtitleText,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                                Text(
                                    text = formattedPrice,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                // Friendly No-Offering fallback state (e.g. products not configured in RC dashboard yet)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CoralPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "RevenueCat Sandbox Notice",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "No live products have been published to your Google Play / RevenueCat dashboard yet. You can test the 21-day free trial and unlock features below using the built-in Demo Mode toggle or simulated purchase.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fallback Plan Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PlanCard(
                        plan = PremiumPlan.ANNUAL,
                        isSelected = fallbackSelectedPlan == PremiumPlan.ANNUAL,
                        onClick = { fallbackSelectedPlan = PremiumPlan.ANNUAL },
                        trialText = "21-Day Free Trial",
                        modifier = Modifier.weight(1f)
                    )

                    PlanCard(
                        plan = PremiumPlan.MONTHLY,
                        isSelected = fallbackSelectedPlan == PremiumPlan.MONTHLY,
                        onClick = { fallbackSelectedPlan = PremiumPlan.MONTHLY },
                        trialText = "21-Day Free Trial",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Feature Comparison Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "MEMBERSHIP PRIVILEGES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Mustard
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PaywallFeatureRow(
                        title = "Daily Perimenopause Symptom Logger",
                        subtitle = "Track all 10 core symptoms & personal triggers daily",
                        isIncludedInFree = true
                    )

                    PaywallFeatureRow(
                        title = "Doctor Dossier & Clinical Export",
                        subtitle = "Full 30-day symptom frequency, burden chart & shareable export",
                        isIncludedInFree = false
                    )

                    PaywallFeatureRow(
                        title = "Rule-Based Correlation Engine",
                        subtitle = "Links sleep deficits, brain fog, peri-rage, and precursor triggers",
                        isIncludedInFree = false
                    )

                    PaywallFeatureRow(
                        title = "Continuous History & Consultation Notes",
                        subtitle = "Ready for OB/GYN and physician appointments",
                        isIncludedInFree = false
                    )
                }
            }

            // Status / Feedback message
            if (statusMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = statusMessage ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TealPrimary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CTA Button (Wires to RevenueCat purchase flow or fallback)
            Button(
                onClick = {
                    if (isPurchasing) return@Button
                    val targetPackage = selectedRcPackage
                    val activity = context.findActivity()

                    if (targetPackage != null && activity != null && Purchases.isConfigured) {
                        isPurchasing = true
                        statusMessage = "Connecting to Google Play..."
                        try {
                            val params = PurchaseParams.Builder(activity, targetPackage).build()
                            Purchases.sharedInstance.purchaseWith(
                                params,
                                onError = { error, userCancelled ->
                                    isPurchasing = false
                                    statusMessage = if (userCancelled) "Purchase cancelled" else error.message
                                },
                                onSuccess = { _, customerInfo ->
                                    isPurchasing = false
                                    revenueCatManager?.processCustomerInfo(customerInfo)
                                    onUpgrade()
                                    onDismiss()
                                }
                            )
                        } catch (e: Exception) {
                            isPurchasing = false
                            statusMessage = "Purchase request: ${e.localizedMessage}"
                            onUpgrade()
                            onDismiss()
                        }
                    } else {
                        // Fallback or demo upgrade
                        onUpgrade()
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("upgrade_premium_cta"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PlumPrimary,
                    contentColor = Color.White
                )
            ) {
                if (isPurchasing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CONNECTING...")
                } else {
                    Text(
                        text = if (isPremium) "MANAGE MEMBERSHIP" else "START 21-DAY FREE TRIAL",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val priceDisclaimer = if (selectedRcPackage != null) {
                "${selectedRcPackage?.product?.price?.formatted} after 21-day trial. Cancel anytime in Google Play."
            } else {
                "${fallbackSelectedPlan.price} after 21-day trial. Cancel anytime in Google Play."
            }

            Text(
                text = priceDisclaimer,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Secondary actions: Restore Purchases and Test Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        if (onRestorePurchases != null) {
                            onRestorePurchases()
                        } else {
                            revenueCatManager?.restorePurchases { success, msg ->
                                statusMessage = msg ?: if (success) "Purchases restored" else "No active subscriptions"
                                if (success) onUpgrade()
                            }
                        }
                    },
                    modifier = Modifier.testTag("paywall_restore_purchases_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Restore Purchases",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Text(
                    "•",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                TextButton(
                    onClick = {
                        if (isPremium) onDowngradeForTesting() else onUpgrade()
                    },
                    modifier = Modifier.testTag("toggle_tier_test_btn")
                ) {
                    Text(
                        if (isPremium) "Test Free Tier" else "Test Premium Tier",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Mustard
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Keep "Simulate Premium (Demo Mode)" toggle available on paywall for hackathon judging
            if (onToggleSimulatePremium != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSimulatedPremium) TealPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSimulatedPremium) TealPrimary else outlineColor.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Simulate Premium (Demo Mode)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Instant demo unlock without Google Play",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Switch(
                            checked = isSimulatedPremium,
                            onCheckedChange = onToggleSimulatePremium,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = TealPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanCard(
    plan: PremiumPlan,
    isSelected: Boolean,
    onClick: () -> Unit,
    trialText: String = "21-Day Free Trial",
    modifier: Modifier = Modifier
) {
    val outlineColor = MaterialTheme.colorScheme.outline
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Mustard else outlineColor,
                shape = RoundedCornerShape(18.dp)
            ),
        color = if (isSelected) PlumPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (plan.savingsBadge != null) {
                Surface(
                    color = BurntOrange,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = plan.savingsBadge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }

            Text(
                text = plan.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = plan.price,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Mustard
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = trialText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Sage,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun PaywallFeatureRow(
    title: String,
    subtitle: String,
    isIncludedInFree: Boolean
) {
    val outlineColor = MaterialTheme.colorScheme.outline
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isIncludedInFree) outlineColor.copy(alpha = 0.5f) else Mustard.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isIncludedInFree) Icons.Default.Check else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isIncludedInFree) MaterialTheme.colorScheme.onSurfaceVariant else Mustard,
                modifier = Modifier.size(12.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            )
        }
    }
}
