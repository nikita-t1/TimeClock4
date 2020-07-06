package com.studio.timeclock4.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.studio.timeclock4.database.dao.AbsenceDao
import com.studio.timeclock4.database.dao.MinimalWorkDayDao
import com.studio.timeclock4.database.dao.WorkDayDao
import com.studio.timeclock4.database.dao.WorkTimeDao
import com.studio.timeclock4.database.entity.AbsenceEntity
import com.studio.timeclock4.database.entity.WorkDayEntity
import com.studio.timeclock4.database.entity.WorkTimeEntity

@Database(
    entities = [WorkDayEntity::class, WorkTimeEntity::class, AbsenceEntity::class],
    version = 1
)
@TypeConverters(Converter::class)
abstract class WorkDatabase : RoomDatabase() {

    abstract fun workDayDao(): WorkDayDao
    abstract fun workTimeDao(): WorkTimeDao
    abstract fun absenceDao(): AbsenceDao
    abstract fun minimalWorkDayDao(): MinimalWorkDayDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: WorkDatabase? = null

        const val DATABASE_NAME = "work_table"

        fun getWorkDatabase(context: Context): WorkDatabase {

            // if the INSTANCE is not null, then return it, if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkDatabase::class.java,
                    DATABASE_NAME
                ).build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
