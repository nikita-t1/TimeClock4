package com.studio.timeclock4.fragments

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.snaptimepicker.SnapTimePickerDialog
import com.akexorcist.snaptimepicker.TimeValue
import com.studio.timeclock4.BuildConfig
import com.studio.timeclock4.R
import com.studio.timeclock4.model.WorkDay
import com.studio.timeclock4.utils.ErrorHandler
import com.studio.timeclock4.utils.ErrorTypes
import com.studio.timeclock4.utils.TimeCalculations.convertDateStringToMinutes
import com.studio.timeclock4.utils.TimeCalculations.convertMinutesToDateString
import com.studio.timeclock4.viewmodel.ListingViewModel
import kotlinx.android.synthetic.main.fragment_edit.*
import org.threeten.bp.Month
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.log10
import com.studio.timeclock4.utils.PreferenceHelper as Pref


class EditFragment(private val workDay: WorkDay, private val databaseAction : DatabaseAction) : DialogFragment(), View.OnClickListener {

    enum class DatabaseAction{
        UPDATE, INSERT, PREVIEW;
    }

    private var start: Long = 0
    private var end: Long = 0
    private var pause: Long = 0

    private lateinit var dialogView: View
    private val workingTimeMin by lazy {Pref.read(Pref.WORKING_TIME, Pref.Default_WORKING_TIME)}
    private val listingViewModel: ListingViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ListingViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogView = inflater.inflate(R.layout.fragment_edit, container, false)
        return dialogView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()

        if (workDay != null){
            workTimeString.text = convertMinutesToDateString(workDay.workTimeGross.toLong())
            startTimeString.text = convertMinutesToDateString(workDay.timeClockIn.toLong())
            endTimeString.text = convertMinutesToDateString(workDay.timeClockOut.toLong())
            pauseTimeString.text = convertMinutesToDateString(workDay.pauseTime.toLong())
            noteString.setText(workDay.userNote)
            date.text = "${workDay.dayOfMonth}. ${Month.of(workDay.month).capitalize()} ${workDay.year}"
        }else{
            ErrorHandler.react(ErrorTypes.ERROR05)
        }
    }

    private fun setListeners() {
        saveBtn.setOnClickListener(this)
        cancelBtn.setOnClickListener(this)
        deleteBtn.setOnClickListener(this)
        startCard.setOnClickListener(this)
        endCard.setOnClickListener(this)
        pauseCard.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        val window = dialog!!.window ?: return
        val params = window.attributes

        val width: Int = Resources.getSystem().displayMetrics.widthPixels
        val height: Int = Resources.getSystem().displayMetrics.heightPixels
        params.width = (width - (width/10))
        params.height = (height - (height/10))
        window.attributes = params

        start = convertDateStringToMinutes(startTimeString.text.toString())
        end = convertDateStringToMinutes(endTimeString.text.toString())
        pause = convertDateStringToMinutes(pauseTimeString.text.toString())
    }

    override fun onClick(v: View?) {
        Timber.i("CLICKED")
        when(v){
            saveBtn -> {
                Timber.i("SAVE")
                val newWorkDay = workDay
                newWorkDay.apply {
                    timeClockIn = convertDateStringToMinutes(startTimeString.text.toString()).toInt()
                    timeClockOut = convertDateStringToMinutes(endTimeString.text.toString()).toInt()
                    pauseTime = convertDateStringToMinutes(pauseTimeString.text.toString()).toInt()
                    userNote = noteString.text.toString()
                    workTimeGross = timeClockOut - timeClockIn
                    workTimeNet = timeClockOut - timeClockIn - pauseTime
                    overtime = ((timeClockOut - timeClockIn) - pauseTime - workingTimeMin).toInt()
                    if (databaseAction == DatabaseAction.UPDATE) listingViewModel.updateWorkDay(newWorkDay)
                    else if (databaseAction == DatabaseAction.INSERT)listingViewModel.insertWorkDay(newWorkDay)
                }
                dismiss()
            }
            cancelBtn -> dismiss()

            deleteBtn -> {
                Timber.i("Delete")
                listingViewModel.deleteWorkDay(workDay)
                dismiss()
            }

            startCard ->{
                val minutes = convertDateStringToMinutes(startTimeString.text.toString())
                val dialog = buildDialog(minutes,5,20)
                dialog.setListener {hour, minute ->
                    dialogView.findViewById<TextView>(R.id.startTimeString)?.text = constructDateString(hour, minute)
                    start = convertDateStringToMinutes(startTimeString.text.toString())
                    calcWorkTime()
                }
                dialog.show(childFragmentManager, tag)
            }
            endCard ->{
                Timber.i("ENDCARD")
                val minutes = convertDateStringToMinutes(endTimeString.text.toString())
                val dialog = buildDialog(minutes, 14,40)
                dialog.setListener {hour, minute ->
                    dialogView.findViewById<TextView>(R.id.endTimeString)?.text = constructDateString(hour, minute)
                    end = convertDateStringToMinutes(endTimeString.text.toString())
                    calcWorkTime()
                }
                dialog.show(childFragmentManager, tag)

            }
            pauseCard ->{
                val minutes = convertDateStringToMinutes(pauseTimeString.text.toString())
                val dialog = buildDialog(minutes, 1,0)
                dialog.setListener {hour, minute ->
                    dialogView.findViewById<TextView>(R.id.pauseTimeString)?.text = constructDateString(hour, minute)
                    pause = convertDateStringToMinutes(pauseTimeString.text.toString())
                    calcWorkTime()
                }
                dialog.show(childFragmentManager, tag)
            }
        }
    }

    private fun constructDateString(hour: Int, minutes: Int) : String{
        val hourString = if (hour.digits() == 1) "0$hour" else "$hour"
        val minuteString = if (minutes.digits() == 1) "0$minutes" else "$minutes"
        return "$hourString:$minuteString"
    }

    private fun buildDialog(minutes: Long, selectedHour: Int, selectedMin: Int): SnapTimePickerDialog {
        val dialogBuilder = SnapTimePickerDialog.Builder().apply {
            if (BuildConfig.DEBUG){
                setPreselectedTime(TimeValue(selectedHour,selectedMin))
            } else setPreselectedTime(TimeValue((minutes/60).toInt(), (minutes%60).toInt()))
        }
        return dialogBuilder.build()
    }

    private fun calcWorkTime(){
        workTimeString.text = convertMinutesToDateString(end - start - pause)
    }
}

private fun Month.capitalize(): String {
    return this.toString().toLowerCase().capitalize()

}

private fun Int.digits() = when(this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}
