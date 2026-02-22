package com.example.liamapp

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.liamapp.notifications.AlarmScheduler
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
                
                // Solicitar permisos de notificación en Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted -> }
                    LaunchedEffect(Unit) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                // Solicitar permiso de alarma exacta en Android 12+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        LaunchedEffect(Unit) {
                            val intent = Intent(
                                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                Uri.parse("package:${packageName}")
                            )
                            startActivity(intent)
                        }
                    }
                }
                
                // Inicializar AlarmScheduler
                val alarmScheduler = AlarmScheduler(this.applicationContext)
                
                val medRepository = MedicationRepository(database.medicationDao())
                val medViewModel: MedicationViewModel = viewModel(
                    factory = MedicationViewModelFactory(medRepository, alarmScheduler)
                )
                
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
                                medicationViewModel = medViewModel,
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
