package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DailyNote
import com.example.data.model.SymptomEntry
import com.example.data.model.SymptomType
import kotlinx.coroutines.flow.Flow

@Dao
interface SymptomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateEntry(entry: SymptomEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEntries(entries: List<SymptomEntry>)

    @Delete
    suspend fun deleteEntry(entry: SymptomEntry)

    @Query("DELETE FROM symptom_entries WHERE date = :date AND symptomType = :symptomType")
    suspend fun deleteByDateAndType(date: String, symptomType: SymptomType)

    @Query("SELECT * FROM symptom_entries WHERE date = :date ORDER BY symptomType ASC")
    fun getEntriesForDate(date: String): Flow<List<SymptomEntry>>

    @Query("SELECT * FROM symptom_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getEntriesBetweenDates(startDate: String, endDate: String): Flow<List<SymptomEntry>>

    @Query("SELECT * FROM symptom_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<SymptomEntry>>

    @Query("SELECT DISTINCT date FROM symptom_entries ORDER BY date DESC")
    fun getDistinctLoggedDates(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyNote(note: DailyNote)

    @Query("SELECT * FROM daily_notes WHERE date = :date LIMIT 1")
    fun getDailyNoteForDate(date: String): Flow<DailyNote?>

    @Query("SELECT * FROM daily_notes WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getDailyNotesBetweenDates(startDate: String, endDate: String): Flow<List<DailyNote>>

    @Query("SELECT COUNT(DISTINCT date) FROM symptom_entries")
    suspend fun getTotalLoggedDaysCount(): Int

    @Query("DELETE FROM symptom_entries")
    suspend fun clearAllEntries()

    @Query("DELETE FROM daily_notes")
    suspend fun clearAllDailyNotes()
}
