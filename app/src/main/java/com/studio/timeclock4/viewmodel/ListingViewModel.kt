package com.studio.timeclock4.viewmodel

import android.app.Application
import android.graphics.Color
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.studio.timeclock4.database.WorkDayDatabase
import com.studio.timeclock4.database.model.WorkDay
import com.studio.timeclock4.repositories.WorkDayRepository
import com.studio.timeclock4.utils.CalendarUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import timber.log.Timber

class ListingViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var lastWeek: LocalDateTime
    var v: Long = 0
    private val _position = MutableLiveData<Int>()
    val position: LiveData<Int> = _position
    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading
    private val _editDialog = MutableLiveData(false)
    val editDialog: LiveData<Boolean> = _editDialog

    private val _monDay = MutableLiveData<WorkDay>()
    private val _tuesDay = MutableLiveData<WorkDay>()
    private val _wednesDay = MutableLiveData<WorkDay>()
    private val _thursDay = MutableLiveData<WorkDay>()
    private val _friDay = MutableLiveData<WorkDay>()
    private val _saturDay = MutableLiveData<WorkDay>()
    private val _sunDay = MutableLiveData<WorkDay>()

    val monDay: LiveData<WorkDay> = _monDay
    val tuesDay: LiveData<WorkDay> = _tuesDay
    val wednesDay: LiveData<WorkDay> = _wednesDay
    val thursDay: LiveData<WorkDay> = _thursDay
    val friDay: LiveData<WorkDay> = _friDay
    val saturDay: LiveData<WorkDay> = _saturDay
    val sunDay: LiveData<WorkDay> = _sunDay

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: WorkDayRepository
    // LiveData gives us updated words when they change.
    val allWorkDays: LiveData<List<WorkDay>>
    lateinit var currentWorkDays: List<WorkDay>
    var emptyWorkDay: WorkDay

    init {
        Timber.i("init ${SystemClock.elapsedRealtime()}")
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val workDayDao =
            WorkDayDatabase.getWorkDayDatabase(application, viewModelScope).workDayDao()
        repository =
            WorkDayRepository(workDayDao)
        allWorkDays = repository.allWorkDays
        Timber.i("init viewmodel")
        emptyWorkDay = WorkDay(
            0, 1, 1, 1,
            1, 1, 0, 0,
            0, 0, 0, 0,
            false, null, "Testtag / Elvis Operator", null
        )
        Timber.i("init ${SystemClock.elapsedRealtime()}")
        lastWeek = LocalDateTime.now()
        allWorkDays.observeForever {
            Timber.i("observeForever")
            updateWorkWeek(lastWeek)
        }
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */

    fun insertWorkDay(workDay: WorkDay) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertWorkDay(workDay)
        }
    }

    fun updateWorkDay(workDay: WorkDay) {
        setEditDialog(false)
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateWorkDay(workDay)
        }
//        updateWorkWeek(lastWeek)
    }

    fun deleteWorkDay(workDay: WorkDay) {
        setEditDialog(false)
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWorkDay(workDay)
        }
//        updateWorkWeek(lastWeek)
    }

    fun deleteAllWorkDays(doYouReallyWantTooDeleteAllWorkDays: Boolean) {
        if (doYouReallyWantTooDeleteAllWorkDays) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteAllWorkDays()
            }
        }
    }

    private fun updateWorkWeek(week: LocalDateTime) {
        lastWeek = week
        viewModelScope.launch() {
            _loading.apply {
                value = true
            }
            Timber.i("3 ${SystemClock.elapsedRealtime() - v}")
            Timber.i("viewModelScope.launch ${Thread.currentThread().name}")

            val mon = async {
                _monDay.apply {
                value =
                    repository.getWorkday(week.dayOfMonth, week.monthValue, week.year) ?: emptyWorkDay
                }
            }
            Timber.i("MO ${SystemClock.elapsedRealtime() - v}")
            val tue = async {
                _tuesDay.apply {
                    value = repository.getWorkday(week.plusDays(1).dayOfMonth, week.plusDays(1).monthValue, week.year)
                        ?: emptyWorkDay
                }
            }
            Timber.i("Di ${SystemClock.elapsedRealtime() - v}")
            val wed = async {
                _wednesDay.apply {
                    value = repository.getWorkday(week.plusDays(2).dayOfMonth, week.plusDays(2).monthValue, week.year)
                        ?: emptyWorkDay
                }
            }
            Timber.i("MI ${SystemClock.elapsedRealtime() - v}")
            val thu = async {
                _thursDay.apply {
                    value = repository.getWorkday(week.plusDays(3).dayOfMonth, week.plusDays(3).monthValue, week.year)
                        ?: emptyWorkDay
                }
            }
            Timber.i("DO ${SystemClock.elapsedRealtime() - v}")
            val fri = async {
                _friDay.apply {
                    value = repository.getWorkday(week.plusDays(4).dayOfMonth, week.plusDays(4).monthValue, week.year)
                        ?: emptyWorkDay
                }
            }
            Timber.i("FR ${SystemClock.elapsedRealtime() - v}")
            val sat = async {
                _saturDay.apply {
                    value = repository.getWorkday(week.plusDays(5).dayOfMonth, week.plusDays(5).monthValue, week.year)
                        ?: emptyWorkDay
                }
            }
            Timber.i("SA ${SystemClock.elapsedRealtime() - v}")
            val sun = async {
                _sunDay.apply {
                    value =
                        repository.getWorkday(week.plusDays(6).dayOfMonth, week.plusDays(6).monthValue, week.year)
                            ?: emptyWorkDay
                }
            }
            Timber.i("SO ${SystemClock.elapsedRealtime() - v}")

            arrayListOf(mon, tue, wed, thu, fri, sat, sun).awaitAll()
            Timber.i("ALL ${SystemClock.elapsedRealtime() - v}")
            _loading.apply {
                value = false
            }
        }
    }

    fun setPosition(position: Int) {
        _position.apply {
            value = position
        }
        v = SystemClock.elapsedRealtime()
        updateWorkWeek(CalendarUtils.startDate.plusWeeks(position.toLong()))
    }

    fun setEditDialog(isOpen: Boolean) {
        _editDialog.apply {
            value = isOpen
        }
    }

    fun weekDayToViewModelElement(dayOfWeek: DayOfWeek): LiveData<WorkDay> {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> monDay
            DayOfWeek.TUESDAY -> tuesDay
            DayOfWeek.WEDNESDAY -> wednesDay
            DayOfWeek.THURSDAY -> thursDay
            DayOfWeek.FRIDAY -> friDay
            DayOfWeek.SATURDAY -> saturDay
            DayOfWeek.SUNDAY -> sunDay
        }
    }

    fun workDayCheck(dayInt: Long): Int {
        val localWorkDay = weekDayToViewModelElement(DayOfWeek.of(dayInt.toInt()))
        return if (localWorkDay.value == emptyWorkDay) {
            Color.RED
        } else Color.GREEN
    }
}
