package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "symptom_entries",
    indices = [
        Index(value = ["date", "symptomType"], unique = true),
        Index(value = ["date"])
    ]
)
data class SymptomEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // ISO-8601 string "yyyy-MM-dd"
    val symptomType: SymptomType,
    val severity: Int, // 1 to 5
    val notes: String = "",
    val triggers: String = "", // Comma-delimited list of triggers e.g. "Red wine,Stress"
    val count: Int = 1, // Optional occurrence count e.g. 3 hot flashes today
    val timestamp: Long = System.currentTimeMillis()
) {
    val triggerList: List<String>
        get() = if (triggers.isBlank()) emptyList() else triggers.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
