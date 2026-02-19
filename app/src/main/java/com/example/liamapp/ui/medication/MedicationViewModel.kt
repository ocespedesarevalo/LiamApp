package com.example.liamapp.ui.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.liamapp.data.MedicationRepository
import com.example.liamapp.data.model.Medication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MedicationUiState(
    val id: Long = 0,
    val consultationId: Long = 0,
    val name: String = "",
    val dose: String = "",
    val frequency: String = "",
    val duration: String = "",
    val purpose: String = "",
    val notes: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val isEditing: Boolean = false,
    val isMedicationSaved: Boolean = false,
    val errorMessage: String? = null
)

class MedicationViewModel(private val repository: MedicationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationUiState())
    val uiState: StateFlow<MedicationUiState> = _uiState.asStateFlow()

    val allMedications: Flow<List<Medication>> = repository.getAllMedications()

    fun getMedicationsForConsultation(consultationId: Long): Flow<List<Medication>> {
        return repository.getMedicationsForConsultation(consultationId)
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onDoseChange(newDose: String) {
        _uiState.update { it.copy(dose = newDose) }
    }

    fun onFrequencyChange(newFrequency: String) {
        if (newFrequency.all { it.isDigit() }) {
            _uiState.update { it.copy(frequency = newFrequency) }
        }
    }

    fun onDurationChange(newDuration: String) {
        if (newDuration.all { it.isDigit() }) {
            _uiState.update { it.copy(duration = newDuration) }
        }
    }

    fun onPurposeChange(newPurpose: String) {
        _uiState.update { it.copy(purpose = newPurpose) }
    }

    fun onNotesChange(newNotes: String) {
        _uiState.update { it.copy(notes = newNotes) }
    }

    fun onStartTimeChange(newTime: Long) {
        _uiState.update { it.copy(startTime = newTime) }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetSavedState() {
        _uiState.update { MedicationUiState() }
    }

    fun resetSavedStateForConsultation(consultationId: Long) {
        _uiState.update { MedicationUiState(consultationId = consultationId) }
    }

    fun startEditing(medication: Medication) {
        _uiState.update {
            it.copy(
                id = medication.id,
                consultationId = medication.consultationId,
                name = medication.name,
                dose = medication.dose,
                frequency = medication.frequencyHours.toString(),
                duration = medication.treatmentDurationDays.toString(),
                purpose = medication.purpose,
                notes = medication.additionalNotes,
                startTime = medication.startTime,
                isEditing = true,
                isMedicationSaved = false
            )
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch {
            repository.deleteMedication(medication)
        }
    }

    fun addOrUpdateMedication() {
        val currentState = _uiState.value
        if (currentState.name.isBlank() || currentState.dose.isBlank() || 
            currentState.frequency.isBlank() || currentState.duration.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor completa todos los campos obligatorios") }
            return
        }

        viewModelScope.launch {
            try {
                val medication = Medication(
                    id = if (currentState.isEditing) currentState.id else 0,
                    consultationId = currentState.consultationId,
                    name = currentState.name,
                    dose = currentState.dose,
                    frequencyHours = currentState.frequency.toIntOrNull() ?: 0,
                    treatmentDurationDays = currentState.duration.toIntOrNull() ?: 0,
                    purpose = currentState.purpose,
                    additionalNotes = currentState.notes,
                    startTime = currentState.startTime
                )
                if (currentState.isEditing) {
                    repository.updateMedication(medication)
                } else {
                    repository.insertMedication(medication)
                }
                _uiState.update { it.copy(isMedicationSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al guardar el medicamento: ${e.message}") }
            }
        }
    }
}

class MedicationViewModelFactory(private val repository: MedicationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MedicationViewModel(repository = repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
