package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.SymptomCognitivePurple
import com.example.ui.theme.SymptomCycleRose
import com.example.ui.theme.SymptomEnergyGreen
import com.example.ui.theme.SymptomExtendedTeal
import com.example.ui.theme.SymptomMoodOrange
import com.example.ui.theme.SymptomPainRed
import com.example.ui.theme.SymptomSleepBlue

enum class SymptomCategory(val label: String) {
    ENERGY("Energy"),                    // green
    SLEEP("Sleep"),                      // blue
    MOOD("Mood"),                        // orange
    COGNITIVE("Cognitive"),              // purple
    PHYSICAL_PAIN("Physical Pain"),      // red
    VASOMOTOR("Temperature & Flushes"), // warm coral
    CYCLE("Cycle & Flow"),               // cycle rose
    EXTENDED("Body & Intimacy")          // extended teal
}

/**
 * Ordered strictly by real perimenopausal frequency:
 * Fatigue, Irritability, Poor Sleep, Brain Fog, Mood Swings/Rage,
 * Hot Flashes, Night Sweats, Irregular Cycle, Joint Pain, Anxiety.
 *
 * Less common symptoms (sexual health, digestion, skin/hair, headaches)
 * are placed in the progressive-disclosure "Add more" section (isExtended = true).
 *
 * Tone of Voice: Validating, normalizing, warm microcopy.
 * (e.g., "Brain fog is a real hormonal symptom, not laziness")
 */
enum class SymptomType(
    val displayName: String,
    val editorialTagline: String,
    val category: SymptomCategory,
    val collectionNumber: String,
    val defaultTriggers: List<String>,
    val isExtended: Boolean = false
) {
    FATIGUE(
        displayName = "Fatigue",
        editorialTagline = "Deep physical fatigue that rest hasn't fully cleared yet",
        category = SymptomCategory.ENERGY,
        collectionNumber = "№ 01",
        defaultTriggers = listOf("Broken sleep", "Hormonal shift", "Full schedule", "Busy week", "Screen fatigue"),
        isExtended = false
    ),
    IRRITABILITY(
        displayName = "Irritability",
        editorialTagline = "A lower sensory threshold and quicker boundary sensitivity today",
        category = SymptomCategory.MOOD,
        collectionNumber = "№ 02",
        defaultTriggers = listOf("Noise", "Multitasking", "Low sleep", "Hormonal dip", "Skipped meal"),
        isExtended = false
    ),
    POOR_SLEEP(
        displayName = "Poor Sleep",
        editorialTagline = "Fragmented rest, 3 AM wakefulness, or restless sleep cycles",
        category = SymptomCategory.SLEEP,
        collectionNumber = "№ 03",
        defaultTriggers = listOf("Screen time", "Active thoughts", "Temperature shift", "Late caffeine", "Stress"),
        isExtended = false
    ),
    BRAIN_FOG(
        displayName = "Brain Fog",
        editorialTagline = "Brain fog is a real hormonal symptom, not laziness",
        category = SymptomCategory.COGNITIVE,
        collectionNumber = "№ 04",
        defaultTriggers = listOf("Sleep debt", "Fast switching", "Dehydration", "Afternoon dip"),
        isExtended = false
    ),
    MOOD_SWINGS_RAGE(
        displayName = "Mood Shifts / Rage",
        editorialTagline = "Sudden surge of intensity or sudden emotional shift — common and real",
        category = SymptomCategory.MOOD,
        collectionNumber = "№ 05",
        defaultTriggers = listOf("Boundary crossing", "Exhaustion", "Estrogen drop", "Overstimulation"),
        isExtended = false
    ),
    HOT_FLASHES(
        displayName = "Hot Flashes",
        editorialTagline = "A sudden internal warmth or flush across the chest and neck",
        category = SymptomCategory.VASOMOTOR,
        collectionNumber = "№ 06",
        defaultTriggers = listOf("Warm room", "Red wine", "Caffeine", "Stress", "Spicy food"),
        isExtended = false
    ),
    NIGHT_SWEATS(
        displayName = "Night Sweats",
        editorialTagline = "Nocturnal temperature surge and waking with damp sleepwear",
        category = SymptomCategory.SLEEP,
        collectionNumber = "№ 07",
        defaultTriggers = listOf("Heavy blanket", "Late meal", "Room temp", "Warm evening"),
        isExtended = false
    ),
    IRREGULAR_CYCLE(
        displayName = "Cycle Shift",
        editorialTagline = "Timing, length, or flow shifted this cycle — typical in this stage",
        category = SymptomCategory.CYCLE,
        collectionNumber = "№ 08",
        defaultTriggers = listOf("Skipped cycle", "Different timing", "Spotting", "Lighter or heavier"),
        isExtended = false
    ),
    JOINT_PAIN(
        displayName = "Joint Aches",
        editorialTagline = "Morning stiffness or tension in fingers, hips, knees or back",
        category = SymptomCategory.PHYSICAL_PAIN,
        collectionNumber = "№ 09",
        defaultTriggers = listOf("Weather shift", "Sitting long", "Active workout", "Cold morning"),
        isExtended = false
    ),
    ANXIETY(
        displayName = "Anxiety & Tension",
        editorialTagline = "Unprompted nervous tension or fluttery heart sensations",
        category = SymptomCategory.MOOD,
        collectionNumber = "№ 10",
        defaultTriggers = listOf("Morning cortisol", "Caffeine", "Overload", "Unknown trigger"),
        isExtended = false
    ),

    // Progressive disclosure extended symptoms
    DIGESTION_BLOATING(
        displayName = "Digestion & Bloating",
        editorialTagline = "Abdominal fullness, sluggish transit, or food sensitivity shifts",
        category = SymptomCategory.EXTENDED,
        collectionNumber = "№ 11",
        defaultTriggers = listOf("Certain foods", "Slower motility", "Hormonal water retention", "Stress"),
        isExtended = true
    ),
    LOW_LIBIDO(
        displayName = "Intimacy & Libido",
        editorialTagline = "Shifts in sexual desire, sensation, or physical vaginal comfort",
        category = SymptomCategory.EXTENDED,
        collectionNumber = "№ 12",
        defaultTriggers = listOf("Dryness", "Fatigue", "Hormonal dip", "Relationship stress"),
        isExtended = true
    ),
    SKIN_HAIR_CHANGES(
        displayName = "Skin & Hair Changes",
        editorialTagline = "Dryness, hormonal breakouts along jawline, or hair thinning",
        category = SymptomCategory.EXTENDED,
        collectionNumber = "№ 13",
        defaultTriggers = listOf("Dry climate", "Collagen shift", "Cleanser change", "Hormonal surge"),
        isExtended = true
    ),
    HEADACHE_MIGRAINE(
        displayName = "Hormonal Headaches",
        editorialTagline = "Throbbing or pressure around temples linked to cycle shifts",
        category = SymptomCategory.PHYSICAL_PAIN,
        collectionNumber = "№ 14",
        defaultTriggers = listOf("Estrogen drop", "Neck strain", "Dehydration", "Bright light"),
        isExtended = true
    );

    val badgeColor: Color
        get() = when (category) {
            SymptomCategory.SLEEP -> SymptomSleepBlue
            SymptomCategory.MOOD -> SymptomMoodOrange
            SymptomCategory.ENERGY -> SymptomEnergyGreen
            SymptomCategory.COGNITIVE -> SymptomCognitivePurple
            SymptomCategory.PHYSICAL_PAIN -> SymptomPainRed
            SymptomCategory.VASOMOTOR -> CoralPrimary
            SymptomCategory.CYCLE -> SymptomCycleRose
            SymptomCategory.EXTENDED -> SymptomExtendedTeal
        }

    companion object {
        fun fromName(name: String): SymptomType? {
            if (name.equals("MOOD_SWINGS", ignoreCase = true)) {
                return MOOD_SWINGS_RAGE
            }
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
        }
    }
}
