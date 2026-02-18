package com.example.liamapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.liamapp.data.dao.MedicationDao
import com.example.liamapp.data.model.Medication
import com.example.liamapp.data.model.MedicationHistory

@Database(entities = [Medication::class, MedicationHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicationDao(): MedicationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medication_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
