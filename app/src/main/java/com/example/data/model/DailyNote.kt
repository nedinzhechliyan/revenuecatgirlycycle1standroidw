package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_notes")
data class DailyNote(
    @PrimaryKey
    val date: String, // "yyyy-MM-dd"
    val menstrualFlow: String = "None", // None, Spotting, Light, Medium, Heavy
    val energyScore: Int = 3, // 1 to 5
    val generalReflection: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
