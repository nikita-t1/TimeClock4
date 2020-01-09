package com.studio.timeclock4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.studio.timeclock4.model.WorkDay
import com.studio.timeclock4.model.WorkDayDatabase
import com.studio.timeclock4.model.WorkDayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListingViewModel(application: Application) : AndroidViewModel(application) {


    val TAG = this.javaClass.simpleName

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: WorkDayRepository
    // LiveData gives us updated words when they change.
    val allWorkDays: LiveData<List<WorkDay>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val workDayDao = WorkDayDatabase.getWorkDayDatabase(application, viewModelScope).workDayDao()
        repository = WorkDayRepository(workDayDao)
        allWorkDays = repository.allWorkDays
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
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateWorkDay(workDay)
        }
    }

    fun deleteWorkDay(workDay: WorkDay) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWorkDay(workDay)
        }
    }

    fun deleteAllWorkDays() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllWorkDays()
        }
    }
}
