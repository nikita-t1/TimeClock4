package com.studio.timeclock4.utils


import android.os.SystemClock
import android.widget.Chronometer
import com.studio.timeclock4.fragments.HomeFragment

class ChronometerPersist private constructor() {
    private var isHourFormat = false

    lateinit var mChronometer: Chronometer
    var mTimeWhenPaused: Long = 0
    var mTimeBase: Long = 0

    val isRunning: Boolean
        get() = ChronometerState.values()[PreferenceHelper.read(
            KEY_STATE + mChronometer.id,
            ChronometerState.Stopped.ordinal
        )] == ChronometerState.Running

    val isPaused: Boolean
        get() = ChronometerState.values()[PreferenceHelper.read(
            KEY_STATE + mChronometer.id,
            ChronometerState.Stopped.ordinal
        )] == ChronometerState.Paused

    internal enum class ChronometerState {
        Running, Paused, Stopped
    }

    fun hourFormat(hourFormat: Boolean) {
        isHourFormat = hourFormat
        if (isHourFormat) {
            mChronometer.onChronometerTickListener = Chronometer.OnChronometerTickListener { c ->
                val elapsedMillis = SystemClock.elapsedRealtime() - c.base
                if (elapsedMillis > 3600000L) {
                    c.format = "0%s"

                } else {
                    c.format = "00:%s"
                }
            }
        } else {

            mChronometer.onChronometerTickListener = null
            mChronometer.format = "%s"
        }
    }

    fun pauseChronometer() {
        storeState(ChronometerState.Paused)
        saveTimeWhenPaused()
        pauseStateChronometer()
    }

    private fun pauseStateChronometer() {
        mTimeWhenPaused = PreferenceHelper.read(
            KEY_TIME_PAUSED + mChronometer.id,
            mChronometer.base - SystemClock.elapsedRealtime()
        )
        //some negative value
        mChronometer.base = SystemClock.elapsedRealtime() + mTimeWhenPaused
        mChronometer.stop()
        if (isHourFormat) {
            val text = mChronometer.text
            if (text.length == 5) {
                mChronometer.text = "00:$text"
            } else if (text.length == 7) {
                mChronometer.text = "0$text"
            }
        }
    }

    private fun storeState(state: ChronometerState) {
        PreferenceHelper.write(KEY_STATE + mChronometer.id, state.ordinal)
    }

    fun startChronometer() {
        storeState(ChronometerState.Running)
        saveBase()
        startStateChronometer()
    }

    private fun startStateChronometer() {
        mTimeBase = PreferenceHelper.read(
            KEY_BASE + mChronometer.id,
            SystemClock.elapsedRealtime()
        ) //0
        mTimeWhenPaused = PreferenceHelper.read(KEY_TIME_PAUSED + mChronometer.id, 0L)
        mChronometer.base = mTimeBase + mTimeWhenPaused
        mChronometer.start()
    }

    fun stopChronometer() {
        storeState(ChronometerState.Stopped)
        mChronometer.base = SystemClock.elapsedRealtime()
        mChronometer.stop()
        if (isHourFormat)
            mChronometer.text = "00:00:00"
        else
            mChronometer.text = "00:00"
        clearState()
    }

    private fun clearState() {
        storeState(ChronometerState.Stopped)
        PreferenceHelper.remove(KEY_BASE + mChronometer.id)
        PreferenceHelper.remove(KEY_TIME_PAUSED + mChronometer.id)
        mTimeWhenPaused = 0
    }

    private fun saveBase() {
        PreferenceHelper.write(KEY_BASE + mChronometer.id, SystemClock.elapsedRealtime())
    }

    private fun saveTimeWhenPaused() {
        PreferenceHelper.write(
            KEY_TIME_PAUSED + mChronometer.id,
            mChronometer.base - SystemClock.elapsedRealtime()
        )
    }

    fun resumeState() {
        val state = ChronometerState.values()[PreferenceHelper.read(
            KEY_STATE + mChronometer.id,
            ChronometerState.Stopped.ordinal
        )]
        if (state.ordinal == ChronometerState.Stopped.ordinal) {
            stopChronometer()
        } else if (state.ordinal == ChronometerState.Paused.ordinal) {
            pauseStateChronometer()
        } else {
            startStateChronometer()
        }
    }

    companion object {

        private val KEY_TIME_PAUSED = "TimePaused"
        private val KEY_BASE = "TimeBase"
        private val KEY_STATE = "ChronometerState"

        fun getInstance(
            chronometer: Chronometer,
            homeFragment: HomeFragment
        ): ChronometerPersist {
            val chronometerPersist = ChronometerPersist()
            PreferenceHelper.init(homeFragment.requireContext())
            chronometerPersist.mChronometer = chronometer
            return chronometerPersist
        }
    }
}