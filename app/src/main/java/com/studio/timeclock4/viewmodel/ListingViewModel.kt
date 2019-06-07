package com.studio.timeclock4.viewmodel

import android.app.Application
import android.util.Log
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

    private val repository: WorkDayRepository
    val allWorkDays: LiveData<List<WorkDay>>

    init {
        val workDayDao = WorkDayDatabase.getWorkDayDatabase(application, viewModelScope).workDayDao()
        repository = WorkDayRepository(workDayDao)
        allWorkDays = repository.allWorkDays
        Log.i(TAG, "*****************************************************")
        Log.d(TAG, "INIT")
        Log.d(TAG, repository.allWorkDays.toString())
        viewModelScope.launch {
            Log.d("tag", workDayDao.selectAll().size.toString())
        }
    }

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
