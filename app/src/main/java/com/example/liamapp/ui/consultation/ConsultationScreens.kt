package com.example.liamapp.ui.consultation

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.liamapp.data.model.Consultation
import com.example.liamapp.data.model.Medication
import com.example.liamapp.ui.medication.MedicationViewModel
import com.example.liamapp.ui.medication.MedicationItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationListScreen(
    navController: NavController, 
    viewModel: ConsultationViewModel,
    medicationViewModel: MedicationViewModel,
    modifier: Modifier = Modifier
) {
    val consultations by viewModel.allConsultations.collectAsState(initial = emptyList())
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES")) }
    var consultationToDelete by remember { mutableStateOf<Consultation?>(null) }

    if (consultationToDelete != null) {
        AlertDialog(
            onDismissRequest = { consultationToDelete = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar esta consulta y todas sus medicinas?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        consultationToDelete?.let { viewModel.deleteConsultation(it) }
                        consultationToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { consultationToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Historial Médico", 
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.resetState()
                    navController.navigate("add_consultation")
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva Consulta") }
            )
        }
    ) { padding ->
        if (consultations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.DateRange, 
                        contentDescription = null, 
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No hay consultas registradas aún",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(consultations) { consultation ->
                    val medsByConsultation by medicationViewModel.getMedicationsForConsultation(consultation.id)
                        .collectAsState(initial = emptyList())
                    
                    ConsultationCard(
                        consultation = consultation,
                        medications = medsByConsultation,
                        dateString = dateFormatter.format(Date(consultation.date)),
                        onEdit = {
                            viewModel.startEditing(consultation)
                            navController.navigate("add_consultation")
                        },
                        onDelete = { consultationToDelete = consultation }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationCard(
    consultation: Consultation,
    medications: List<Medication>,
    dateString: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // Lógica de colorimetría:
    // Rojo: Falta información clave (Doctor o Diagnóstico vacío)
    // Naranja: Faltan medicamentos asociados
    // Verde: Todo completo
    val cardColor = when {
        consultation.doctorName.isBlank() || consultation.diagnosis.isBlank() -> Color(0xFFFFEBEE) // Rojo muy suave
        medications.isEmpty() -> Color(0xFFFFF3E0) // Naranja muy suave
        else -> Color(0xFFE8F5E9) // Verde muy suave
    }
    
    val indicatorColor = when {
        consultation.doctorName.isBlank() || consultation.diagnosis.isBlank() -> Color(0xFFD32F2F) // Rojo
        medications.isEmpty() -> Color(0xFFF57C00) // Naranja
        else -> Color(0xFF388E3C) // Verde
    }

    ElevatedCard(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(indicatorColor, RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if(consultation.doctorName.isBlank()) "Sin Doctor" else consultation.doctorName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = "Borrar",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info, 
                    contentDescription = null,
                    tint = indicatorColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if(consultation.diagnosis.isBlank()) "Falta Diagnóstico" else consultation.diagnosis,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Estado de medicamentos
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.List, 
                    contentDescription = null,
                    tint = if(medications.isEmpty()) indicatorColor else Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if(medications.isEmpty()) "Sin medicamentos recetados" else "${medications.size} medicamentos añadidos",
                    style = MaterialTheme.typography.bodySmall,
                    color = if(medications.isEmpty()) indicatorColor else Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = indicatorColor.copy(alpha = 0.1f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = if (indicatorColor == Color(0xFF388E3C)) "Completo" else "Completar >",
                    style = MaterialTheme.typography.labelLarge,
                    color = indicatorColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddConsultationScreen(
    navController: NavController, 
    viewModel: ConsultationViewModel,
    medicationViewModel: MedicationViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES")) }

    val medications by medicationViewModel.getMedicationsForConsultation(uiState.id)
        .collectAsState(initial = emptyList())

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
            viewModel.resetState()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Detalles de Consulta" else "Nueva Consulta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Información de la Consulta",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.doctorName,
                    onValueChange = viewModel::onDoctorNameChange,
                    label = { Text("Nombre del Doctor") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Person, null) }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = uiState.diagnosis,
                    onValueChange = viewModel::onDiagnosisChange,
                    label = { Text("Diagnóstico") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Info, null) }
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedCard(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = uiState.date
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val newCalendar = Calendar.getInstance()
                                newCalendar.set(year, month, day)
                                viewModel.onDateChange(newCalendar.timeInMillis)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DateRange, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Fecha de Consulta", style = MaterialTheme.typography.labelMedium)
                            Text(
                                dateFormatter.format(Date(uiState.date)), 
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::onNotesChange,
                    label = { Text("Receta / Notas Médicas") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Divider()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Medicamentos", 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (uiState.isEditing) {
                        Button(
                            onClick = { 
                                medicationViewModel.resetSavedStateForConsultation(uiState.id)
                                navController.navigate("add_medication") 
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Añadir")
                        }
                    }
                }
                
                if (!uiState.isEditing) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(
                            text = "Guarda la consulta primero para poder añadir medicamentos.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (uiState.isEditing) {
                if (medications.isEmpty()) {
                    item {
                        Text(
                            "No se han añadido medicamentos a esta receta.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    items(medications) { medication ->
                        MedicationItem(
                            medication = medication,
                            onEdit = {
                                medicationViewModel.startEditing(medication)
                                navController.navigate("add_medication")
                            },
                            onDelete = { medicationViewModel.deleteMedication(medication) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.saveConsultation() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) { 
                    Text(
                        if (uiState.isEditing) "Guardar Cambios" else "Crear Consulta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    ) 
                }
            }
        }
    }
}
