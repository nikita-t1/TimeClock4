package com.studio.timeclock4.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                ).addCallback(WorkDayDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    private class WorkDayDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        /**
         * Override the onOpen method to populate the database.
         * For this sample, we clear the database every time it is created or opened.
         */
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            // If you want to keep the data through app restarts,
            // comment out the following line.
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.workDayDao())
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more words, just add them.
         */
        suspend fun populateDatabase(workDayDao: WorkDayDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            workDayDao.deleteAllWorkDays()

            var workDay = WorkDay(
                0, 2018, 24, 1,
                24, 3, "07:14", "16:34",
                "0:45", "8:55", "8:34", "1:34",
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2019, 24, 2,
                25, 3, "06:18", "15:42",
                "0:45", "8:33", "7:10", "0:10",
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2019, 24, 3,
                26, 3, "07:18", "17:42",
                "0:45", "9:33", "07:00", "0",
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2019, 24, 4,
                27, 3, "06:18", "15:42",
                "0:45", "8:33", "7:46", "0.43",
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)

            workDay = WorkDay(
                0, 2019, 24, 1,
                24, 3, "07:14", "16:34",
                "0:45", "8:55", "8:34", "1:34",
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2019, 24, 2,
                25, 3, "06:18", "15:42",
                "0:45", "8:33", "7:10", "0:10",
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2019, 24, 3,
                26, 3, "07:18", "17:42",
                "0:45", "9:33", "07:00", "0",
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2017, 24, 4,
                27, 3, "06:18", "15:42",
                "0:45", "8:33", "7:46", "0.43",
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
        }
    }
}
