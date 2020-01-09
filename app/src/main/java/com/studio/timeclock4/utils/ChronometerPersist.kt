package com.studio.timeclock4.utils


import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import com.studio.timeclock4.fragments.HomeFragment

class ChronometerPersist private constructor() {
    private var isHourFormat = false
    val TAG = this.javaClass.simpleName
    lateinit var mChronometer: Chronometer
    var mTimeWhenPaused: Long = 0
    var mTimeBase: Long = 0

    fun changeChronometerState(state: Int) {
        when (state) {
            0 -> stopChronometer()
            1 -> startChronometer()
            2 -> pauseChronometer()
        }
    }

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

                when {
                    elapsedMillis < 3600000L -> c.format = "00:%s"
                    elapsedMillis < 3.6e+7 -> c.format = "0%s"
                    else -> c.format = "%s"
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
        Log.i(TAG, "${SystemClock.elapsedRealtime()}")
        Log.i(TAG, "KEY_BASE + mChronometer.id Read: ${PreferenceHelper.read(KEY_BASE + mChronometer.id, 10L)}")
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
        Log.i(
            TAG,
            "KEY_BASE + mChronometer.id Write before: ${PreferenceHelper.read(KEY_BASE + mChronometer.id, 10L)}"
        );
        PreferenceHelper.write(KEY_BASE + mChronometer.id, SystemClock.elapsedRealtime())
        Log.i(TAG, "KEY_BASE + mChronometer.id Write after: ${PreferenceHelper.read(KEY_BASE + mChronometer.id, 10L)}");
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
        when {
            state.ordinal == ChronometerState.Stopped.ordinal -> stopChronometer()
            state.ordinal == ChronometerState.Paused.ordinal -> pauseStateChronometer()
            else -> startStateChronometer()
        }
    }

    companion object {

        private const val KEY_TIME_PAUSED = "TimePaused"
        private const val KEY_BASE = "TimeBase"
        private const val KEY_STATE = "ChronometerState"

        fun getInstance(chronometer: Chronometer, homeFragment: HomeFragment): ChronometerPersist {
            val chronometerPersist = ChronometerPersist()
            PreferenceHelper.init(homeFragment.requireContext())
            chronometerPersist.mChronometer = chronometer
            return chronometerPersist
        }
    }
}