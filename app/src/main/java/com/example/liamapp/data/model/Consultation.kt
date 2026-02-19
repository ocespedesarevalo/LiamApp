package com.example.liamapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consultations")
data class Consultation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val doctorName: String = "",
    val diagnosis: String = "",
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)
