package com.example.liamapp.data

import com.example.liamapp.data.dao.MedicationDao
import com.example.liamapp.data.model.Medication
import com.example.liamapp.data.model.MedicationHistory
import kotlinx.coroutines.flow.Flow

class MedicationRepository(private val medicationDao: MedicationDao) {

    fun getAllMedications(): Flow<List<Medication>> = medicationDao.getAllMedications()

    fun getMedicationsForConsultation(consultationId: Long): Flow<List<Medication>> = 
        medicationDao.getMedicationsForConsultation(consultationId)

    suspend fun getMedicationById(id: Long): Medication? = medicationDao.getMedicationById(id)

    fun getMedicationHistory(medicationId: Long): Flow<List<MedicationHistory>> = medicationDao.getMedicationHistory(medicationId)

    suspend fun insertMedication(medication: Medication): Long {
        return medicationDao.insertMedication(medication)
    }

    suspend fun updateMedication(medication: Medication) {
        medicationDao.updateMedication(medication)
    }

    suspend fun deleteMedication(medication: Medication) {
        medicationDao.deleteMedication(medication)
    }

    suspend fun insertMedicationHistory(history: MedicationHistory): Long {
        return medicationDao.insertMedicationHistory(history)
    }
}
