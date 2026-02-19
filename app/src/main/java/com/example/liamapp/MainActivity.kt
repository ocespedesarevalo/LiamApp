package com.example.liamapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.liamapp.data.AppDatabase
import com.example.liamapp.data.MedicationRepository
import com.example.liamapp.ui.SplashScreen
import com.example.liamapp.ui.consultation.AddConsultationScreen
import com.example.liamapp.ui.consultation.ConsultationListScreen
import com.example.liamapp.ui.consultation.ConsultationViewModel
import com.example.liamapp.ui.consultation.ConsultationViewModelFactory
import com.example.liamapp.ui.medication.AddMedicationScreen
import com.example.liamapp.ui.medication.MedicationViewModel
import com.example.liamapp.ui.medication.MedicationViewModelFactory
import com.example.liamapp.ui.theme.LiamAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Instalar Splash Screen de la API oficial antes de super.onCreate
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        setContent {
            LiamAppTheme {
                val navController = rememberNavController()
                val database = AppDatabase.getDatabase(this.applicationContext)
                
                val medRepository = MedicationRepository(database.medicationDao())
                val medViewModel: MedicationViewModel = viewModel(factory = MedicationViewModelFactory(medRepository))
                
                val consViewModel: ConsultationViewModel = viewModel(factory = ConsultationViewModelFactory(database.consultationDao()))

                NavHost(
                    navController = navController, 
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen(onNavigateToMain = {
                            navController.navigate("consultation_list") {
                                popUpTo("splash") { inclusive = true }
                            }
                        })
                    }
                    composable("consultation_list") { 
                        Scaffold { innerPadding ->
                            ConsultationListScreen(
                                navController = navController, 
                                viewModel = consViewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                    composable("add_consultation") { 
                        Scaffold { innerPadding ->
                            AddConsultationScreen(
                                navController = navController, 
                                viewModel = consViewModel, 
                                medicationViewModel = medViewModel,
                                modifier = Modifier.padding(innerPadding)
                            ) 
                        }
                    }
                    composable("add_medication") { 
                        Scaffold { innerPadding ->
                            AddMedicationScreen(
                                navController = navController, 
                                viewModel = medViewModel,
                                modifier = Modifier.padding(innerPadding)
                            ) 
                        }
                    }
                }
            }
        }
    }
}
