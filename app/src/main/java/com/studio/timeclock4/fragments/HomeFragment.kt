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
                Timber.i("EDIT")
            }
            fragmentView.cardView -> {
                Timber.i("CARD")
            }
            fragmentView.overviewBtn -> {
                Timber.i("OVERVIEW")
                chronometerPersist.substractFromChronometerBase(60)
            }
            fragmentView.attendanceBtn -> {
                Timber.i( "ATTENDANCE")
                chronometerPersist.addToChronometerBase(60)
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
        })
        viewModel.startButtonColor.observe(viewLifecycleOwner, Observer {
            fragmentView.startButton.background.setTint(
                resources.getColor(it, null)
            )
        })
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
}
