package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.SymptomType

class Converters {
    @TypeConverter
    fun fromSymptomType(value: SymptomType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toSymptomType(value: String?): SymptomType? {
        return value?.let { SymptomType.fromName(it) }
    }
}
