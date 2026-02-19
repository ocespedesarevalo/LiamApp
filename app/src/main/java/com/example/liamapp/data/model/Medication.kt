package com.example.liamapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medications",
    foreignKeys = [
        ForeignKey(
            entity = Consultation::class,
            parentColumns = ["id"],
            childColumns = ["consultationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["consultationId"])]
)
data class Medication(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val consultationId: Long, // Relación con la consulta
    val name: String,
    val dose: String,
    val frequencyHours: Int,
    val treatmentDurationDays: Int,
    val purpose: String,
    val additionalNotes: String,
    val startTime: Long
)
