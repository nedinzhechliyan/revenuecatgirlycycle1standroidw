package com.example.data.repository

import com.example.data.local.SymptomDao
import com.example.data.model.DailyNote
import com.example.data.model.SymptomEntry
import com.example.data.model.SymptomType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class GirlyCycleRepository(private val dao: SymptomDao) {

    val distinctLoggedDates: Flow<List<String>> = dao.getDistinctLoggedDates()
    val allEntries: Flow<List<SymptomEntry>> = dao.getAllEntries()

    fun getEntriesForDate(date: String): Flow<List<SymptomEntry>> {
        return dao.getEntriesForDate(date)
    }

    fun getEntriesBetweenDates(startDate: String, endDate: String): Flow<List<SymptomEntry>> {
        return dao.getEntriesBetweenDates(startDate, endDate)
    }

    fun getDailyNote(date: String): Flow<DailyNote?> {
        return dao.getDailyNoteForDate(date)
    }

    suspend fun saveSymptom(
        date: String,
        symptomType: SymptomType,
        severity: Int,
        notes: String = "",
        triggers: String = "",
        count: Int = 1
    ) = withContext(Dispatchers.IO) {
        val entry = SymptomEntry(
            date = date,
            symptomType = symptomType,
            severity = severity,
            notes = notes,
            triggers = triggers,
            count = count
        )
        dao.insertOrUpdateEntry(entry)
    }

    suspend fun removeSymptom(date: String, symptomType: SymptomType) = withContext(Dispatchers.IO) {
        dao.deleteByDateAndType(date, symptomType)
    }

    suspend fun saveDailyNote(
        date: String,
        flow: String,
        energyScore: Int,
        reflection: String
    ) = withContext(Dispatchers.IO) {
        val note = DailyNote(
            date = date,
            menstrualFlow = flow,
            energyScore = energyScore,
            generalReflection = reflection
        )
        dao.insertOrUpdateDailyNote(note)
    }

    suspend fun seedSampleHistoryIfEmpty(referenceDate: LocalDate = LocalDate.now()) = withContext(Dispatchers.IO) {
        val count = dao.getTotalLoggedDaysCount()
        if (count >= 5) return@withContext

        val entriesToInsert = mutableListOf<SymptomEntry>()
        val notesToInsert = mutableListOf<DailyNote>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Seed 28 days of history leading up to yesterday
        for (i in 28 downTo 1) {
            val date = referenceDate.minusDays(i.toLong())
            val dateStr = date.format(formatter)

            // Fatigue (highest real frequency)
            val hasFatigue = (i % 5 != 0) // ~80% of days
            if (hasFatigue) {
                entriesToInsert.add(
                    SymptomEntry(
                        date = dateStr,
                        symptomType = SymptomType.FATIGUE,
                        severity = Random.nextInt(2, 5),
                        notes = if (i % 3 == 0) "Hit a wall around 2:30 PM, heavy limbs" else "Drained despite lying in bed",
                        triggers = if (i % 2 == 0) "Broken sleep,Busy week" else "High workload"
                    )
                )
            }

            // Poor Sleep (high frequency)
            val hasPoorSleep = (i % 3 != 0) || (i % 4 == 0)
            if (hasPoorSleep) {
                entriesToInsert.add(
                    SymptomEntry(
                        date = dateStr,
                        symptomType = SymptomType.POOR_SLEEP,
                        severity = Random.nextInt(3, 5),
                        notes = if (i % 2 == 0) "Wide awake at 3:15 AM with racing thoughts" else "Fitful light sleep, felt unrested",
                        triggers = "Racing thoughts,Temperature shift"
                    )
                )
            }

            // Brain Fog (correlated with poor sleep)
            if (hasPoorSleep && (i % 2 == 0 || i % 3 == 1)) {
                entriesToInsert.add(
                    SymptomEntry(
                        date = dateStr,
                        symptomType = SymptomType.BRAIN_FOG,
                        severity = Random.nextInt(2, 5),
                        notes = "Lost my train of thought mid-sentence during discussion",
                        triggers = "Sleep debt,Afternoon dip"
                    )
                )
            }

            // Irritability
            if (hasPoorSleep && i % 3 == 0) {
                entriesToInsert.add(
                    SymptomEntry(
                        date = dateStr,
                        symptomType = SymptomType.IRRITABILITY,
                        severity = Random.nextInt(2, 4),
                        notes = "Zero patience for background noise today",
                        triggers = "Noise,Low sleep"
                    )
                )
            }

            // Mood Swings / Rage
            if (i % 4 == 2) {
                entriesToInsert.add(
                    SymptomEntry(
                        date = dateStr,
                        symptomType = SymptomType.MOOD_SWINGS_RAGE,
                        severity = Random.nextInt(3, 5),
                        notes = "Sudden flash of pure rage over misplaced keys, followed by tears",
                        triggers = "Overstimulation,Estrogen drop"
                    )
                )
            }

            // Hot Flashes
            val hasHotFlashes = (i % 2 == 1)
            if (hasHotFlashes) {
                entriesToInsert.add(
                    SymptomEntry(
                        date = dateStr,
                        symptomType = SymptomType.HOT_FLASHES,
                        severity = Random.nextInt(2, 5),
                        notes = "Sudden intense chest heat during meeting",
                        triggers = if (i % 3 == 0) "Red wine,Stress" else "Warm room,Caffeine",
                        count = Random.nextInt(2, 5)
                    )
                )
            }

            // Night Sweats
            if (hasHotFlashes && (i % 3 == 0)) {
                entriesToInsert.add(
                    SymptomEntry(
                        date = dateStr,
                        symptomType = SymptomType.NIGHT_SWEATS,
                        severity = Random.nextInt(3, 5),
                        notes = "Woke up drenched around 3 AM, flipped pillow and duvet",
                        triggers = "Heavy blanket,Alcohol"
                    )
                )
            }

            // Joint Pain
            if (i % 4 == 1) {
                entriesToInsert.add(
                    SymptomEntry(
                        date = dateStr,
                        symptomType = SymptomType.JOINT_PAIN,
                        severity = Random.nextInt(2, 4),
                        notes = "Stiff knuckles and achy right hip getting out of bed",
                        triggers = "Weather change,Inactivity"
                    )
                )
            }

            // Anxiety
            if (i % 5 == 3) {
                entriesToInsert.add(
                    SymptomEntry(
                        date = dateStr,
                        symptomType = SymptomType.ANXIETY,
                        severity = Random.nextInt(2, 4),
                        notes = "Chest flutter and unprovoked morning dread before getting up",
                        triggers = "Morning cortisol,Caffeine"
                    )
                )
            }

            // Irregular cycle on days 12-14
            if (i in 12..14) {
                entriesToInsert.add(
                    SymptomEntry(
                        date = dateStr,
                        symptomType = SymptomType.IRREGULAR_CYCLE,
                        severity = 3,
                        notes = "Spotting 16 days after previous light bleed",
                        triggers = "Skipped cycle,Spotting"
                    )
                )
                notesToInsert.add(
                    DailyNote(
                        date = dateStr,
                        menstrualFlow = if (i == 13) "Medium" else "Spotting",
                        energyScore = 2,
                        generalReflection = "Unpredictable cycle timing this month. 42 days since last full cycle."
                    )
                )
            } else if (i % 7 == 0) {
                notesToInsert.add(
                    DailyNote(
                        date = dateStr,
                        menstrualFlow = "None",
                        energyScore = if (hasPoorSleep) 2 else 4,
                        generalReflection = "Cool shower and magnesium glycinate before bed helped settle restless legs."
                    )
                )
            }
        }

        dao.insertAllEntries(entriesToInsert)
        for (note in notesToInsert) {
            dao.insertOrUpdateDailyNote(note)
        }
    }

    suspend fun resetDatabase() = withContext(Dispatchers.IO) {
        dao.clearAllEntries()
        dao.clearAllDailyNotes()
    }
}
