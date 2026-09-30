package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SymptomEntry
import com.example.data.model.SymptomType
import com.example.domain.DoctorReportGenerator
import com.example.domain.RuleBasedInsightsEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("GirlyCycle", appName)
    }

    @Test
    fun `rule-based insights detects poor sleep and brain fog correlation`() {
        val entries = listOf(
            SymptomEntry(date = "2026-09-20", symptomType = SymptomType.POOR_SLEEP, severity = 4),
            SymptomEntry(date = "2026-09-20", symptomType = SymptomType.BRAIN_FOG, severity = 4),
            SymptomEntry(date = "2026-09-21", symptomType = SymptomType.POOR_SLEEP, severity = 3),
            SymptomEntry(date = "2026-09-21", symptomType = SymptomType.BRAIN_FOG, severity = 3),
            SymptomEntry(date = "2026-09-22", symptomType = SymptomType.POOR_SLEEP, severity = 5),
            SymptomEntry(date = "2026-09-22", symptomType = SymptomType.BRAIN_FOG, severity = 4)
        )

        val insights = RuleBasedInsightsEngine.generateInsights(entries, emptyList())
        val brainFogInsight = insights.find { it.id == "sleep_brain_fog" }

        assertNotNull(brainFogInsight)
        assertTrue(brainFogInsight!!.headline.contains("poor sleep on 3 of the last 3 days you also logged brain fog"))
        assertTrue(brainFogInsight.validatingNote.contains("This pattern is real and worth bringing to your doctor"))
    }

    @Test
    fun `doctor report generates formatted clinical summary`() {
        val entries = listOf(
            SymptomEntry(date = "2026-09-25", symptomType = SymptomType.HOT_FLASHES, severity = 4, count = 3, notes = "flushed face during meeting"),
            SymptomEntry(date = "2026-09-25", symptomType = SymptomType.FATIGUE, severity = 3, notes = "drained by 2pm")
        )

        val report = DoctorReportGenerator.generateReport(
            patientName = "Helen",
            userStage = "Perimenopause • Year 3",
            endDate = LocalDate.of(2026, 9, 26),
            windowDays = 30,
            entries = entries,
            dailyNotes = emptyList()
        )

        assertEquals("Helen", report.patientName)
        assertEquals(1, report.totalLoggedDays)
        assertTrue(report.formattedShareableSummary.contains("GIRLYCYCLE ATELIER • DOCTOR VISIT DOSSIER"))
        assertTrue(report.formattedShareableSummary.contains("HOT FLASHES"))
    }

    @Test
    fun `entitlement manager calculates 21-day trial and unlock states correctly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = com.example.data.repository.DefaultEntitlementManager.getInstance(context)

        // Reset to fresh start
        val today = LocalDate.now()
        manager.resetTrial(today = today)
        manager.setSubscribed(false)

        // Day 1: 21 days remaining, trial messaging active
        val stateDay1 = manager.entitlementState.value
        assertEquals(21, stateDay1.daysRemaining)
        assertEquals(1, stateDay1.currentTrialDay)
        assertTrue(stateDay1.isTrialActive)
        // Features are gated until premium entitlement is active (replacing local trial-date logic)
        assertTrue(!stateDay1.isDoctorDossierUnlocked)
        assertTrue(!stateDay1.isInsightsUnlocked)
        assertTrue(stateDay1.isDailyLogForeverFree)

        // Simulate Day 14: 8 days remaining, nudge active
        manager.simulateTrialDay(14)
        val stateDay14 = manager.entitlementState.value
        assertEquals(8, stateDay14.daysRemaining)
        assertEquals(14, stateDay14.currentTrialDay)
        assertTrue(stateDay14.isTrialActive)
        assertTrue(stateDay14.showDay14Nudge)

        // Dismiss day 14 nudge
        manager.dismissDay14Nudge()
        assertTrue(!manager.entitlementState.value.showDay14Nudge)

        // Simulate Day 22: Expired trial
        manager.simulateTrialDay(22)
        val stateDay22 = manager.entitlementState.value
        assertEquals(0, stateDay22.daysRemaining)
        assertTrue(stateDay22.isTrialExpired)
        assertTrue(!stateDay22.isTrialActive)
        assertTrue(!stateDay22.isDoctorDossierUnlocked)
        assertTrue(!stateDay22.isInsightsUnlocked)
        assertTrue(stateDay22.isDailyLogForeverFree) // Forever free!
        assertTrue(stateDay22.showDay21ExpiryDialog)

        // Subscribing unlocks dossier and insights even after trial expiration
        manager.setSubscribed(true)
        val stateSubscribed = manager.entitlementState.value
        assertTrue(stateSubscribed.isSubscribed)
        assertTrue(stateSubscribed.isDoctorDossierUnlocked)
        assertTrue(stateSubscribed.isInsightsUnlocked)
    }

    @Test
    fun `simulate premium demo mode toggle unlocks dossier and insights for demos`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = com.example.data.repository.DefaultEntitlementManager.getInstance(context)

        manager.setSubscribed(false)
        manager.setSimulatePremium(false)
        assertTrue(!manager.entitlementState.value.isDoctorDossierUnlocked)
        assertTrue(!manager.entitlementState.value.isInsightsUnlocked)
        assertTrue(!manager.hasEntitlement(com.example.data.repository.AppEntitlement.DOCTOR_DOSSIER))

        // Toggle on Demo Mode
        manager.setSimulatePremium(true)
        val stateDemo = manager.entitlementState.value
        assertTrue(stateDemo.isSimulatedPremium)
        assertTrue(stateDemo.isSubscribed)
        assertTrue(stateDemo.isDoctorDossierUnlocked)
        assertTrue(stateDemo.isInsightsUnlocked)
        assertTrue(manager.hasEntitlement(com.example.data.repository.AppEntitlement.DOCTOR_DOSSIER))
        assertTrue(manager.hasEntitlement(com.example.data.repository.AppEntitlement.INSIGHTS))
        assertTrue(manager.hasEntitlement(com.example.data.repository.AppEntitlement.ATELIER_MEMBERSHIP))
        assertEquals("Atelier Membership Active (Demo Mode)", stateDemo.trialBadgeText)

        // Toggle off Demo Mode
        manager.setSimulatePremium(false)
        val stateReverted = manager.entitlementState.value
        assertTrue(!stateReverted.isSimulatedPremium)
        assertTrue(!stateReverted.isDoctorDossierUnlocked)
        assertTrue(!stateReverted.isInsightsUnlocked)
    }
}
