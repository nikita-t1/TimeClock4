package com.studio.timeclock4

import android.app.Application
import android.graphics.Color
import android.widget.Toast
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.Logger.addLogAdapter
import com.orhanobut.logger.PrettyFormatStrategy
import com.studio.timeclock4.database.Converter
import com.studio.timeclock4.database.dao.SharedPreferencesDao
import com.studio.timeclock4.repositories.SharedPreferencesRepository
import com.studio.timeclock4.utils.ErrorHandler
import com.studio.timeclock4.utils.ErrorTypes
import com.studio.timeclock4.utils.PreferenceHelper
import jonathanfinerty.once.Once
import jp.wasabeef.takt.Seat
import jp.wasabeef.takt.Takt
import java.time.OffsetDateTime
import timber.log.Timber
import timber.log.Timber.DebugTree

open class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PreferenceHelper.init(this)
        SharedPreferencesDao.init(this)
        ErrorHandler.init(this)
        Once.initialise(this)
        val repository = SharedPreferencesRepository(SharedPreferencesDao, Converter())

        if (BuildConfig.DEBUG) {

            // Output Style for Logger
            val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
                .methodCount(1) // (Optional) How many method line to show. Default 2
                .methodOffset(5) // (Optional) Hides internal method calls up to offset. Default 5
                .tag("") // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build()
            addLogAdapter(AndroidLogAdapter(formatStrategy))

            // Creates a Frames Indicator and Listener
            val taktProgramm = Takt.stock(this).color(Color.BLACK).seat(Seat.TOP_LEFT).listener {
                if (it < PreferenceHelper.DEV_MinFrames) ErrorHandler.react(ErrorTypes.ERROR03)
            }

            // Plants Timber DebugTree
            Timber.plant(object : DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    Logger.log(priority, tag, message, t)
                }
            })

            // Hides Frames Indicator (keeps Listener)
            if (!PreferenceHelper.read(PreferenceHelper.DEV_EnableFrames, false)) taktProgramm.hide()
        } else {
            // Plants Timber ReleaseTree
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // Do nothing
                }
            })
        }

        // Runs only on first launch after install
        if (!Once.beenDone(Once.THIS_APP_INSTALL, "setStartDate")) {
            repository.installDate = OffsetDateTime.now()
            Toast.makeText(this, "${repository.installDate}", Toast.LENGTH_LONG).show()
            Once.markDone("setStartDate")
        }
    }
}
