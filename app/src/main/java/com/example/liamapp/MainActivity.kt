package com.example.liamapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.liamapp.data.AppDatabase
import com.example.liamapp.data.MedicationRepository
import com.example.liamapp.ui.medication.AddMedicationScreen
import com.example.liamapp.ui.medication.MedicationViewModel
import com.example.liamapp.ui.medication.MedicationViewModelFactory
import com.example.liamapp.ui.theme.LiamAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiamAppTheme {
                val navController = rememberNavController()

                // Conectamos la base de datos y el repositorio
                val database = AppDatabase.getDatabase(this)
                val repository = MedicationRepository(database.medicationDao())

                NavHost(navController = navController, startDestination = "add_medication") {
                    composable("add_medication") {
                        AddMedicationScreen(
                            navController = navController,
                            viewModel = viewModel<MedicationViewModel>(
                                factory = MedicationViewModelFactory(repository)
                            )
                        )
                    }
                }
            }
        }
    }
}