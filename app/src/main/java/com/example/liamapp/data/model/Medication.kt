package com.example.liamapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dose: String,
    val frequencyHours: Int,
    val treatmentDurationDays: Int,
    val purpose: String,
    val additionalNotes: String,
    val startTime: Long // Timestamp de la primera toma
)
