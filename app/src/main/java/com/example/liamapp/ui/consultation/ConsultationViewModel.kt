package com.example.liamapp.ui.consultation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.liamapp.data.dao.ConsultationDao
import com.example.liamapp.data.model.Consultation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConsultationUiState(
    val id: Long = 0,
    val doctorName: String = "",
    val diagnosis: String = "",
    val date: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isEditing: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class ConsultationViewModel(private val consultationDao: ConsultationDao) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsultationUiState())
    val uiState: StateFlow<ConsultationUiState> = _uiState.asStateFlow()

    val allConsultations: Flow<List<Consultation>> = consultationDao.getAllConsultations()

    fun onDoctorNameChange(newName: String) { _uiState.update { it.copy(doctorName = newName) } }
    fun onDiagnosisChange(newDiagnosis: String) { _uiState.update { it.copy(diagnosis = newDiagnosis) } }
    fun onDateChange(newDate: Long) { _uiState.update { it.copy(date = newDate) } }
    fun onNotesChange(newNotes: String) { _uiState.update { it.copy(notes = newNotes) } }

    fun resetState() { _uiState.value = ConsultationUiState() }

    fun startEditing(consultation: Consultation) {
        _uiState.update {
            it.copy(
                id = consultation.id,
                doctorName = consultation.doctorName,
                diagnosis = consultation.diagnosis,
                date = consultation.date,
                notes = consultation.notes,
                isEditing = true,
                isSaved = false
            )
        }
    }

    fun deleteConsultation(consultation: Consultation) {
        viewModelScope.launch { consultationDao.deleteConsultation(consultation) }
    }

    fun saveConsultation() {
        val state = _uiState.value
        if (state.doctorName.isBlank() || state.diagnosis.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nombre del doctor y diagnóstico son obligatorios") }
            return
        }

        viewModelScope.launch {
            val consultation = Consultation(
                id = if (state.isEditing) state.id else 0,
                doctorName = state.doctorName,
                diagnosis = state.diagnosis,
                date = state.date,
                notes = state.notes
            )
            if (state.isEditing) consultationDao.updateConsultation(consultation)
            else consultationDao.insertConsultation(consultation)
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}

class ConsultationViewModelFactory(private val consultationDao: ConsultationDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConsultationViewModel(consultationDao) as T
    }
}
