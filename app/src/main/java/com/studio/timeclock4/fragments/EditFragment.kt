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
import com.studio.timeclock4.utils.PreferenceHelper
import com.studio.timeclock4.utils.TimeCalculations.convertMinutesToDateString
import com.studio.timeclock4.utils.TimeCalculations.convertDateStringToMinutes
import com.studio.timeclock4.viewmodel.ListingViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_edit.*
import org.threeten.bp.Month
import timber.log.Timber


class EditFragment(private val workDay: WorkDay, private val databaseAction : DatabaseAction) : DialogFragment(), View.OnClickListener {

    enum class DatabaseAction{
        UPDATE, INSERT, PREVIEW;
    }

    private var start: Long = 0
    private var end: Long = 0
    private var pause: Long = 0

    private lateinit var dialogView: View
    private val workingTimeMin by lazy { PreferenceHelper.read(PreferenceHelper.working_time, 456L)   } //7,6h
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
        saveBtn.setOnClickListener(this)
        cancelBtn.setOnClickListener(this)
        deleteBtn.setOnClickListener(this)
        startCard.setOnClickListener(this)
        endCard.setOnClickListener(this)
        pauseCard.setOnClickListener(this)

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
//                    else
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
                val dialogBuilder = SnapTimePickerDialog.Builder().apply {
                    if (BuildConfig.DEBUG){
                        setPreselectedTime(TimeValue(5,20))
                    } else setPreselectedTime(TimeValue((minutes/60).toInt(), (minutes%60).toInt()))
                }
                val dialog = dialogBuilder.build()
                dialog.setListener {hour, minute ->
                    Toasty.info(requireContext(), "", Toasty.LENGTH_SHORT).show()
                    val hourString = if (hour <= 9) "0$hour" else "$hour"
                    val minuteString = if (minute <= 9) "0$minute" else "$minute"
                    dialogView.findViewById<TextView>(R.id.startTimeString)?.text = "$hourString:$minuteString"
                    start = convertDateStringToMinutes(startTimeString.text.toString())
                    calcWorkTime()
                }
                dialog.show(childFragmentManager, tag)
            }
            endCard ->{
                Timber.i("ENDCARD")
                val minutes = convertDateStringToMinutes(endTimeString.text.toString())
                val dialogBuilder = SnapTimePickerDialog.Builder().apply {
                    if (BuildConfig.DEBUG){
                        setPreselectedTime(TimeValue(14,40))
                    } else setPreselectedTime(TimeValue((minutes/60).toInt(), (minutes%60).toInt()))
                    setTitle(R.string.set_start_time)
                    setThemeColor(R.color.light_pink)
                    setTitleColor(R.color.grey)
                }
                val dialog = dialogBuilder.build()
                dialog.setListener {hour, minute ->
                    val hourString = if (hour <= 9) "0$hour" else "$hour"
                    val minuteString = if (minute <= 9) "0$minute" else "$minute"
                    dialogView.findViewById<TextView>(R.id.endTimeString)?.text = "$hourString:$minuteString"
                    end = convertDateStringToMinutes(endTimeString.text.toString())
                    calcWorkTime()
                }
                dialog.show(childFragmentManager, tag)

            }
            pauseCard ->{
                val minutes = convertDateStringToMinutes(pauseTimeString.text.toString())
                val dialogBuilder = SnapTimePickerDialog.Builder().apply {
                    if (BuildConfig.DEBUG){
                        setPreselectedTime(TimeValue(1,0))
                    } else setPreselectedTime(TimeValue((minutes/60).toInt(), (minutes%60).toInt()))
                }
                val dialog = dialogBuilder.build()
                dialog.setListener {hour, minute ->
                    val hourString = if (hour <= 9) "0$hour" else "$hour"
                    val minuteString = if (minute <= 9) "0$minute" else "$minute"
                    dialogView.findViewById<TextView>(R.id.pauseTimeString)?.text = "$hourString:$minuteString"
                    pause = convertDateStringToMinutes(pauseTimeString.text.toString())
                    calcWorkTime()
                }
                dialog.show(childFragmentManager, tag)
            }
        }
    }

    private fun calcWorkTime(){
        workTimeString.text = convertMinutesToDateString(end - start - pause)
    }


    override fun onDetach() {
        super.onDetach()
        Timber.e("DETACH")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.e("DESTROY")
    }
}

private fun Month.capitalize(): String {
    return this.toString().toLowerCase().capitalize()

}
