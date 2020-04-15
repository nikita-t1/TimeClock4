package com.studio.timeclock4.fragments

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.*
import com.studio.timeclock4.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import timber.log.Timber


class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var chronometerPersist: ChronometerPersist
    private lateinit var fragmentView: View
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onClick(v: View?) {
        when (v) {
            fragmentView.startButton -> {
                viewModel.startPressed()
                chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
            }
            fragmentView.editButton -> {
                val dialog = EditFragment(
                    viewModel.createTemporaryWorkDay(),
                    EditFragment.DatabaseAction.PREVIEW
                )
                showEditDialog(dialog)
            }
            fragmentView.cardView -> {
                Timber.i("CARD")
            }
            fragmentView.overviewBtn -> {
                Timber.i("OVERVIEW")
            }
            fragmentView.attendanceBtn -> {
                Timber.i( "ATTENDANCE")
            }
            fragmentView.arc_progress -> {
                Timber.i( "ARC")
            }
            else -> Timber.e( "Something went wrong in the onClick")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_home, container, false)

        fragmentView.startButton.setOnClickListener(this)
        fragmentView.editButton.setOnClickListener(this)
        fragmentView.cardView.setOnClickListener(this)
        fragmentView.overviewBtn.setOnClickListener(this)
        fragmentView.attendanceBtn.setOnClickListener(this)
        fragmentView.arc_progress.setOnClickListener(this)

        chronometerPersist = ChronometerPersist.getInstance(fragmentView.chrom)
        chronometerPersist.hourFormat(true)

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(viewModel)

        viewModel.startButtonText.observe(viewLifecycleOwner, Observer { fragmentView.startButton.text = it })
        viewModel.startTimeString.observe(viewLifecycleOwner, Observer { fragmentView.startTime.text = it })
        viewModel.endTimeString.observe(viewLifecycleOwner, Observer { fragmentView.endTime.text = it })
        viewModel.currentLayoutStateOrdinal.observe(viewLifecycleOwner, Observer {
            if (it.ordinal == 2) showSaveDialog()
            editButton.isClickable = it.ordinal == 1
        })
        viewModel.startButtonColor.observe(viewLifecycleOwner, Observer {
            fragmentView.startButton.background.setTint(
                resources.getColor(it, null)
            )
        })
    }

    private fun showEditDialog(dialog : EditFragment) {
        dialog.setTargetFragment(this, 0)
        dialog.show(parentFragmentManager, "dialog")
    }

    override fun onResume() {
        super.onResume()
        chronometerPersist.resumeState()
        Timber.i("IM BACK BITCHES")
    }

    private fun showSaveDialog() {
        val time = ((SystemClock.elapsedRealtime() - chronometerPersist.mChronometer.base) / 1000 / 60)
        val timeString = TimeCalculations.convertMinutesToDateString(time)

        val alert = AlertView(timeString, resources.getString(R.string.clock_out_question), BottomSheetStyle.BOTTOM_SHEET)
        alert.addAction(AlertAction(resources.getString(R.string.clock_out), BottomSheetActionStyle.POSITIVE) {
            viewModel.dialogSave(time)
            chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
        })
        alert.addAction(AlertAction(resources.getString(R.string.resume), BottomSheetActionStyle.DEFAULT) {
            viewModel.dialogResume()
            chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
        })
        alert.addAction(AlertAction(resources.getString(R.string.abort), BottomSheetActionStyle.NEGATIVE) {
            viewModel.dialogCancel()
            chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
        })
        alert.show(childFragmentManager)
    }

    fun receiveNewWorkDayValues(startTimeMin: Int, pauseTime: Int, userNote: String) {
        val currentStartTime = PreferenceHelper.read(PreferenceHelper.CURRENT_START_TIME, PreferenceHelper.Default_START_TIME)
        Timber.e("new: $startTimeMin , old: $currentStartTime")
        if (currentStartTime < startTimeMin){
            val difference = startTimeMin - currentStartTime
            chronometerPersist.substractFromChronometerBase(difference*60)
        } else if (currentStartTime > startTimeMin){
            val difference = currentStartTime - startTimeMin
            chronometerPersist.addToChronometerBase(difference*60)
        }
        viewModel.setNewWorkDayValues(startTimeMin, pauseTime, userNote)
    }
}
