package com.example.liamapp.ui.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.liamapp.data.MedicationRepository
import com.example.liamapp.data.model.Medication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MedicationUiState(
    val name: String = "",
    val dose: String = "",
    val frequency: String = "",
    val duration: String = "",
    val purpose: String = "",
    val notes: String = "",
    val isMedicationSaved: Boolean = false,
    val errorMessage: String? = null
)

class MedicationViewModel(private val repository: MedicationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationUiState())
    val uiState: StateFlow<MedicationUiState> = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onDoseChange(newDose: String) {
        _uiState.update { it.copy(dose = newDose) }
    }

    fun onFrequencyChange(newFrequency: String) {
        _uiState.update { it.copy(frequency = newFrequency) }
    }

    fun onDurationChange(newDuration: String) {
        _uiState.update { it.copy(duration = newDuration) }
    }

    fun onPurposeChange(newPurpose: String) {
        _uiState.update { it.copy(purpose = newPurpose) }
    }

    fun onNotesChange(newNotes: String) {
        _uiState.update { it.copy(notes = newNotes) }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun addMedication() {
        val currentState = _uiState.value
        if (currentState.name.isBlank() || currentState.dose.isBlank() || 
            currentState.frequency.isBlank() || currentState.duration.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor completa todos los campos obligatorios") }
            return
        }

        viewModelScope.launch {
            try {
                val medication = Medication(
                    name = currentState.name,
                    dose = currentState.dose,
                    frequencyHours = currentState.frequency.toIntOrNull() ?: 0,
                    treatmentDurationDays = currentState.duration.toIntOrNull() ?: 0,
                    purpose = currentState.purpose,
                    additionalNotes = currentState.notes,
                    startTime = System.currentTimeMillis()
                )
                repository.insertMedication(medication)
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
