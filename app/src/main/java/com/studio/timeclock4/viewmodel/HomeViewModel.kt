package com.studio.timeclock4.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.studio.timeclock4.R
import com.studio.timeclock4.database.Converter
import com.studio.timeclock4.database.WorkDatabase
import com.studio.timeclock4.database.dao.SharedPreferencesDao
import com.studio.timeclock4.database.dao.WorkDayDao
import com.studio.timeclock4.database.dao.WorkTimeDao
import com.studio.timeclock4.database.entity.WorkTimeEntity
import com.studio.timeclock4.repositories.CurrentSessionRepository
import com.studio.timeclock4.repositories.SharedPreferencesRepository
import com.studio.timeclock4.repositories.WorkDayRepository
import com.studio.timeclock4.repositories.WorkTimeRepository
import com.studio.timeclock4.utils.format
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import timber.log.Timber

class HomeViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {
    val app = application

    // TODO: Inject Fields with KOIN
    private var workTimeDao: WorkTimeDao =
        WorkDatabase.getWorkDatabase(application).workTimeDao()
    private var workDayDao: WorkDayDao =
        WorkDatabase.getWorkDatabase(application).workDayDao()
    private val workDayRepository = WorkDayRepository(workDayDao)
    private var sharedPrefDao = SharedPreferencesDao
    private val sharedPrefRepository = SharedPreferencesRepository(sharedPrefDao, Converter())
    private val workTimeRepo = WorkTimeRepository(workTimeDao, workDayRepository, sharedPrefRepository)
    private val currentSessionRepo = CurrentSessionRepository(sharedPrefDao, Converter())

    private val _startTime = MutableLiveData<String>()
    val startTimeString: LiveData<String> = _startTime
    private val _endTime = MutableLiveData<String>()
    val endTimeString: LiveData<String> = _endTime
    private val _pauseTime = MutableLiveData<String>()
    val pauseTimeString: LiveData<String> = _pauseTime
    private val _remainingText = MutableLiveData<String>()
    val remainingText: LiveData<String> = _remainingText
    private val _currentLayoutStateOrdinal = MutableLiveData<LayoutState>()
    val currentLayoutStateOrdinal: LiveData<LayoutState> = _currentLayoutStateOrdinal

    // TODO: Migrate to Theme Attributes -> "?attr/"
    private val _startButtonText = MutableLiveData<String>()
    val startButtonText: LiveData<String> = _startButtonText
    private val _startButtonColor = MutableLiveData<Int>()
    val startButtonColor: LiveData<Int> = _startButtonColor

    private val _chronometerFormat = MutableLiveData<String>()
    val chronometerFormat: LiveData<String> = _chronometerFormat
    private val _progressBarDay = MutableLiveData<Int>()
    val progressBarDay: LiveData<Int> = _progressBarDay
    private val _progressBarWeek = MutableLiveData<Int>()
    val progressBarWeek: LiveData<Int> = _progressBarWeek

    private val pattern = DateTimeFormatter.ofPattern("HH:mm")

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Timber.i("RESUME")
    }

    enum class LayoutState {
        Ready, Tracking, Saving;

        companion object {
            fun next(state: LayoutState): LayoutState {
                return when (state) {
                    Ready -> Tracking
                    Tracking -> Saving
                    Saving -> Ready
                }
            }

            fun getState(state: Int): LayoutState {
                return values()[state]
            }
        }
    }

    init {
        Timber.w("init")
        _currentLayoutStateOrdinal.apply {
            value = LayoutState.values()[currentSessionRepo.layoutState]
        }

        _startTime.value = currentSessionRepo.startTime.format(pattern)
        _endTime.value = currentSessionRepo.endTime.format(pattern)
        _pauseTime.value = currentSessionRepo.currentPauseTime.format(pattern)

        updateLayoutState()
    }

    fun startPressed() {
        _currentLayoutStateOrdinal.apply {
            value = LayoutState.next(LayoutState.getState(currentLayoutStateOrdinal.value!!.ordinal))
        }
        if (currentLayoutStateOrdinal.value == LayoutState.Tracking) {
            currentSessionRepo.startTime = OffsetDateTime.now()
        }
        updateLayoutState()
    }

    private fun updateLayoutState() {
        currentSessionRepo.layoutState = currentLayoutStateOrdinal.value!!.ordinal

        _startTime.value = currentSessionRepo.startTime.format(pattern)
        _endTime.value = currentSessionRepo.endTime.format(pattern)
        _pauseTime.value = currentSessionRepo.currentPauseTime.format(pattern)

        when (currentLayoutStateOrdinal.value) {
            LayoutState.Ready -> {
                _startButtonColor.apply { value = R.color.green }
                _startButtonText.apply { value = app.resources.getString(R.string.start) }
            }
            LayoutState.Tracking -> {
                _startButtonColor.apply { value = R.color.red }
                _startButtonText.apply { value = app.resources.getString(R.string.stop) }

                val expectedEndTime = loadEndTime(true)
                currentSessionRepo.endTime = expectedEndTime
            }
            LayoutState.Saving -> {
                _startButtonColor.apply { value = R.color.red }
                _startButtonText.apply { value = app.resources.getString(R.string.stop) }
            }
        }
    }

    private fun loadEndTime(addPause: Boolean): OffsetDateTime {
        val workTimeNet = currentSessionRepo.startTime + sharedPrefRepository.baseTime
        return when (addPause) {
            true -> workTimeNet + currentSessionRepo.currentPauseTime
            false -> workTimeNet
        }
    }

    fun onChronometerClick(elapsedMillis: Long, isHourFormat: Boolean) {
        val elapsedMin = elapsedMillis / 1000 / 60.0
        if (isHourFormat) {
            when {
                elapsedMillis < 3600000L -> _chronometerFormat.value = "00:%s"
                elapsedMillis < 3.6e+7 -> _chronometerFormat.value = "0%s"
                else -> _chronometerFormat.value = "%s"
            }
        } else {
            _chronometerFormat.value = "%s"
        }

        _progressBarDay.value = kotlin.math.floor(elapsedMillis / (
                sharedPrefRepository.baseTime.toMillis() + sharedPrefRepository.pauseTime.toMillis()
                ) * 100.toDouble()).toInt()
        _remainingText.value = (
                sharedPrefRepository.baseTime.toMillis() +
                        sharedPrefRepository.pauseTime.toMillis() -
                        elapsedMillis
                ).format(pattern, ChronoUnit.MILLIS)
    }

    fun dialogCancel() {
        _currentLayoutStateOrdinal.apply { value = LayoutState.Ready }
        currentSessionRepo.remove()
        updateLayoutState()
    }

    fun dialogSave(endTime: Long) {
        viewModelScope.launch {
            val workTime = WorkTimeEntity(
                workTimeID = 0,
                workTimeOwnerDate = LocalDate.now(),
                timeClockIn = currentSessionRepo.startTime,
                timeClockOut = currentSessionRepo.startTime.plusMinutes(endTime),
                userNote = currentSessionRepo.currentNote
            )
            workTimeRepo.insertWorkTime(workTime)
        }
        dialogCancel()
    }

    fun dialogResume() {
        _currentLayoutStateOrdinal.apply { value = LayoutState.Tracking }
        updateLayoutState()
    }

    fun createTemporaryWorkDay(): WorkTimeEntity {
        val dateTime = OffsetDateTime.now().withHour(0).withMinute(0)
        return WorkTimeEntity(
            workTimeID = 0,
            workTimeOwnerDate = LocalDate.now(),
            timeClockIn = dateTime,
            timeClockOut = dateTime,
            userNote = ""
        )
    }

    fun setNewWorkDayValues(newStartTime: OffsetDateTime, newPauseTime: Duration, newNote: String) {
        currentSessionRepo.apply {
            startTime = newStartTime
            currentPauseTime = newPauseTime
            currentNote = newNote
        }
        updateLayoutState()
    }
}
