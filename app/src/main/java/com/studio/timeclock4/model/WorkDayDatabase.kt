package com.studio.timeclock4.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.studio.timeclock4.utils.PreferenceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@Database(entities = [WorkDay::class], version = 1)
abstract class WorkDayDatabase : RoomDatabase() {

    abstract fun workDayDao(): WorkDayDao

    /*
    This class has more Code then TimeClock3 because of the Kotlin
        Null-Safety feature
     */
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: WorkDayDatabase? = null

        fun getWorkDayDatabase(context: Context, scope: CoroutineScope): WorkDayDatabase {

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
