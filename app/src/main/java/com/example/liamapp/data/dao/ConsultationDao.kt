package com.example.liamapp.data.dao

import androidx.room.*
import com.example.liamapp.data.model.Consultation
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultationDao {
    @Insert
    suspend fun insertConsultation(consultation: Consultation): Long

    @Update
    suspend fun updateConsultation(consultation: Consultation)

    @Delete
    suspend fun deleteConsultation(consultation: Consultation)

    @Query("SELECT * FROM consultations ORDER BY date DESC")
    fun getAllConsultations(): Flow<List<Consultation>>
}
