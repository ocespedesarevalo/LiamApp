package com.example.liamapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.liamapp.data.dao.MedicationDao
import com.example.liamapp.data.dao.ConsultationDao
import com.example.liamapp.data.model.Medication
import com.example.liamapp.data.model.MedicationHistory
import com.example.liamapp.data.model.Consultation

@Database(entities = [Medication::class, MedicationHistory::class, Consultation::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicationDao(): MedicationDao
    abstract fun consultationDao(): ConsultationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medication_database"
                )
                .fallbackToDestructiveMigration() // Para facilitar el desarrollo con cambios en el esquema
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
