package com.example.liamapp.ui.medication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AddMedicationScreen(navController: NavController, viewModel: MedicationViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Navigate back when the medication is saved successfully
    LaunchedEffect(uiState.isMedicationSaved) {
        if (uiState.isMedicationSaved) {
            navController.popBackStack()
        }
    }

    // Show a snackbar for any error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.errorMessageShown() // Reset the error message
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre del medicamento") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.dose,
                onValueChange = viewModel::onDoseChange,
                label = { Text("Dosis (ej. 5ml, 1 tableta)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.frequency,
                onValueChange = viewModel::onFrequencyChange,
                label = { Text("Frecuencia (horas)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.duration,
                onValueChange = viewModel::onDurationChange,
                label = { Text("Duración del tratamiento (días)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.purpose,
                onValueChange = viewModel::onPurposeChange,
                label = { Text("¿Para qué sirve?") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notas adicionales") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.addMedication() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Medicamento")
            }
        }
    }
}