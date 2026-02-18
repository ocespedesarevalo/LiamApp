package com.example.liamapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.liamapp.data.model.Medication
import com.example.liamapp.data.model.MedicationHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Insert
    suspend fun insertMedication(medication: Medication): Long

    @Insert
    suspend fun insertMedicationHistory(history: MedicationHistory): Long

    @Query("SELECT * FROM medications")
    fun getAllMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM medication_history WHERE medicationId = :medicationId ORDER BY timestamp DESC")
    fun getMedicationHistory(medicationId: Long): Flow<List<MedicationHistory>>
}
