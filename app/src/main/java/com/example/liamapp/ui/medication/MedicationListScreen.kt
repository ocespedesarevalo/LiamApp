package com.example.liamapp.ui.medication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.liamapp.data.model.Medication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationListScreen(navController: NavController, viewModel: MedicationViewModel) {
    val medications by viewModel.allMedications.collectAsState(initial = emptyList())
    var medicationToDelete by remember { mutableStateOf<Medication?>(null) }

    // Diálogo de confirmación de borrado
    if (medicationToDelete != null) {
        AlertDialog(
            onDismissRequest = { medicationToDelete = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar '${medicationToDelete?.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        medicationToDelete?.let { viewModel.deleteMedication(it) }
                        medicationToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { medicationToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Medicamentos") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                viewModel.resetSavedState()
                navController.navigate("add_medication") 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        if (medications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay medicamentos registrados aún.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(medications) { medication ->
                    MedicationItem(
                        medication = medication,
                        onEdit = {
                            viewModel.startEditing(medication)
                            navController.navigate("add_medication")
                        },
                        onDelete = {
                            medicationToDelete = medication
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun MedicationItem(medication: Medication, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = medication.name, style = MaterialTheme.typography.titleLarge)
                Text(text = "Dosis: ${medication.dose}")
                Text(text = "Cada ${medication.frequencyHours} horas")
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar")
                }
            }
        }
    }
}
