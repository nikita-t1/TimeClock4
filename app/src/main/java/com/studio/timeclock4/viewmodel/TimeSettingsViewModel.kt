package com.studio.timeclock4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.TimeCalculations
import es.dmoral.toasty.Toasty
import timber.log.Timber
import kotlin.math.ceil
import kotlin.math.pow
import com.studio.timeclock4.utils.PreferenceHelper as Pref

class TimeSettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application
    private val hourShort = app.resources.getString(R.string.hour_short)
    private val days = app.resources.getString(R.string.days)

    enum class InputField{
        workingTime, workingTimeWeek, pauseTime,
        flexAccount, vacation
    }

    val hsl = hourShort.length
    val dl = days.length

    private val _workingTime = MutableLiveData<String>()
    val workingTime: LiveData<String> = _workingTime
    private val _workingTimeWeek = MutableLiveData<String>()
    val workingTimeWeek: LiveData<String> = _workingTimeWeek
    private val _pauseTime = MutableLiveData<String>()
    val pauseTime: LiveData<String> = _pauseTime
    private val _flexAccount = MutableLiveData<String>()
    val flexAccount: LiveData<String> = _flexAccount
    private val _vacation = MutableLiveData<String>()
    val vacation: LiveData<String> = _vacation



    init {
        setValues()
        Toasty.info(app, Pref.read(Pref.WORKING_TIME, 0L).toString() , Toasty.LENGTH_SHORT).show()
    }

    private fun setValues(){
        _workingTime.apply {
//            value = "${(Pref.read(Pref.WORKING_TIME, Pref.Default_WORKING_TIME).toFloat() / 60f).toInt()}.${(Pref.read(Pref.WORKING_TIME, 480L).toFloat() % 60f /60*100).toInt()} $hourShort"
            value = TimeCalculations.convertMinutesToDateString(Pref.read(Pref.WORKING_TIME, Pref.Default_WORKING_TIME))
        }
        _workingTimeWeek.apply {
            value = "${(Pref.read(Pref.WORKING_TIME_WEEK, Pref.Default_WORKING_TIME_WEEK).toFloat() / 60f).toInt()}." +
                    "${(Pref.read(Pref.WORKING_TIME_WEEK, Pref.Default_WORKING_TIME_WEEK).toFloat() % 60f /60*100).toInt()} $hourShort"
        }
         _pauseTime.apply {
             value = TimeCalculations.convertMinutesToDateString(Pref.read(Pref.PAUSE_TIME, Pref.Default_PAUSE_TIME))
        }
        _flexAccount.apply {
            value = "${(Pref.read(Pref.FLEX_ACCOUNT, Pref.Default_FLEX_ACCOUNT).toFloat() / 60f).toInt()}." +
                    "${(Pref.read(Pref.FLEX_ACCOUNT, Pref.Default_FLEX_ACCOUNT).toFloat() % 60f /60*100).toInt()} $hourShort"
        }
        _vacation.apply {
            value = "${Pref.read(Pref.VACATION, Pref.Default_VACATION)} $days"
        }
    }

    fun updateField(field: InputField, text: Long) {
        kotlin.runCatching {
            when (field) {
                InputField.workingTime -> Pref.write(Pref.WORKING_TIME, text)
                InputField.workingTimeWeek -> Pref.write(Pref.WORKING_TIME_WEEK, text)
                InputField.pauseTime -> Pref.write(Pref.PAUSE_TIME, text)
                InputField.flexAccount -> Pref.write(Pref.FLEX_ACCOUNT, text)
                InputField.vacation -> Pref.write(Pref.VACATION, text)
            }
        }
        setValues()
        Timber.e("HOIERRR ${text}")
    }

    fun verifyText(field: InputField, text: String): Array<Any> {
        var min = 0
        if (!text.isBlank() && !text.startsWith(".") && !text.endsWith(".")) {
            if (field != InputField.vacation) {
                val isFloat = text.contains(".")
                val parts: Array<String>?
                min = if (isFloat) {
                    parts = text.split(".").toTypedArray()
                        (parts[0].toInt() * 60 +
                                (ceil(
                                    (60 * (parts[1].toInt())).toDouble() / (10.toDouble().pow(parts[1].length))
                                ))
                        ).toInt()
                } else {
                    text.toInt() * 60
                }
                Timber.e("ÜÜÜ $min")
                min = when (field) {
                    InputField.workingTime -> inRange(min, 10, 23 * 60, 8 * 60)
                    InputField.workingTimeWeek -> inRange(min, 60, 23 * 60 * 7, 8 * 60 * 5)
                    InputField.pauseTime -> inRange(min, 5, 20 * 60, 1 * 60)
                    InputField.flexAccount -> inRange(min, -250 * 60, 250 * 60, 1)
                    InputField.vacation -> TODO()
                }
            } else {
                var days = 1
                if (!text.contains(".") && text.length <=2){
                    days = text.toInt()
                }
                min = days
            }
            when(field) {
                InputField.workingTime -> updateField(
                    InputField.workingTimeWeek,
                    min * Pref.read(Pref.WORKING_DAYS_WEEK, Pref.Default_WORKING_DAYS_WEEK).toLong()
                )
                InputField.workingTimeWeek -> updateField(
                    InputField.workingTime,
                    ceil(min / Pref.read(Pref.WORKING_DAYS_WEEK, Pref.Default_WORKING_DAYS_WEEK).toDouble()).toLong()
                )
            }
            return arrayOf(min, true)

        } else return arrayOf(min, false) //min verify vacation
    }

    private fun inRange(input: Int, min: Int, max: Int, default: Int): Int {
        return if (input.coerceAtLeast(min) > min && input.coerceAtMost(max) < max){
            input
        }else {
            Toasty.info(app, "Ausserhalb des gültigen Bereiches", Toasty.LENGTH_SHORT).show()
            default
        }

    }

    fun updateWorkWeek(dayChip: String, isChecked: Boolean) {
        var workingDaysAmount = Pref.read(Pref.WORKING_DAYS_WEEK, Pref.Default_WORKING_DAYS_WEEK)
        if (workingDaysAmount > 7) Toasty.info(app, "ZUVIELE TAGE", Toasty.LENGTH_SHORT).show()
        Timber.i("$workingDaysAmount")
        when (isChecked){
            true -> Pref.write(Pref.WORKING_DAYS_WEEK, (workingDaysAmount + 1))
            false -> Pref.write(Pref.WORKING_DAYS_WEEK, (workingDaysAmount - 1))
        }
        workingDaysAmount = Pref.read(Pref.WORKING_DAYS_WEEK, Pref.Default_WORKING_DAYS_WEEK)
        Timber.i(workingDaysAmount.toString())
        Pref.write(dayChip, isChecked)
        Timber.e("(Pref.read(Pref.WORKING_TIME, Pref.Default_WORKING_TIME) ${(Pref.read(Pref.WORKING_TIME, Pref.Default_WORKING_TIME))} ")
        updateField(InputField.workingTimeWeek, (Pref.read(Pref.WORKING_TIME, Pref.Default_WORKING_TIME) * workingDaysAmount))
    }
}
