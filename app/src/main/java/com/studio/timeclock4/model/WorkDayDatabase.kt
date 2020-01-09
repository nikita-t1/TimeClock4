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
                    if (PreferenceHelper.read("enable database recreation", false)) {
                        populateDatabase(database.workDayDao())
                    }
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
            Timber.e("DATABASE")

            var workDay = WorkDay(
                12, 2020, 14, 5,
                3, 4, 378, 942,
                45, 513, 430, 10,
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)

            workDay = WorkDay(
                11, 2020, 9, 1,
                24, 2, 408, 942,
                45, 513, 466, 43,
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2020, 8, 5,
                21, 2, 378, 942,
                45, 513, 466, 43,
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2020, 9, 7,
                2, 3, 370, 942,
                45, 485, 466, 43,
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2020, 10, 7,
                8, 3, 435, 932,
                45, 485, 466, 13,
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2020, 11, 7,
                9, 3, 435, 932,
                45, 485, 466, 13,
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2020, 11, 6,
                14, 3, 435, 932,
                45, 485, 466, 13,
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2020, 11, 6,
                10, 3, 435, 932,
                45, 485, 466, 13,
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
            workDay = WorkDay(
                0, 2020, 11, 6,
                1, 2, 435, 932,
                45, 485, 466, 13,
                true, null, null, null
            )
            workDayDao.insertWorkDay(workDay)
        }
    }
}
