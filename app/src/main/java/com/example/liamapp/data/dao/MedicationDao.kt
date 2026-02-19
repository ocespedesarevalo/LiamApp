package com.example.liamapp.data.dao

import androidx.room.*
import com.example.liamapp.data.model.Medication
import com.example.liamapp.data.model.MedicationHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: Medication): Long

    @Update
    suspend fun updateMedication(medication: Medication)

    @Delete
    suspend fun deleteMedication(medication: Medication)

    @Query("SELECT * FROM medications")
    fun getAllMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE consultationId = :consultationId")
    fun getMedicationsForConsultation(consultationId: Long): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: Long): Medication?

    @Insert
    suspend fun insertMedicationHistory(history: MedicationHistory): Long

    @Query("SELECT * FROM medication_history WHERE medicationId = :medicationId ORDER BY timestamp DESC")
    fun getMedicationHistory(medicationId: Long): Flow<List<MedicationHistory>>
}
