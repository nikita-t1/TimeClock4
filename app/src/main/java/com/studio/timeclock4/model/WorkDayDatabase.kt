package com.studio.timeclock4.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WorkDay::class], version = 1)
abstract class WorkDayDatabase : RoomDatabase() {

    abstract fun workDayDao(): WorkDayDao

    /*
    This class has more Code then TimeClock3 because of the Kotlin
        Null-Safety feature
     */
    companion object {
        @Volatile
        private var INSTANCE: WorkDayDatabase? = null

        fun getWorkDayDatabase(context: Context): WorkDayDatabase {

            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkDayDatabase::class.java,
                    "workday_database"
                ).build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
