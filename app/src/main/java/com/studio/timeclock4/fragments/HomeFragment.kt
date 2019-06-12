package com.studio.timeclock4.fragments

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.ChronometerPersist
import com.studio.timeclock4.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment(), View.OnClickListener {


    private lateinit var alertDialog: AlertDialog
    private lateinit var chronometerPersist: ChronometerPersist
    private val TAG = this.javaClass.simpleName
    private lateinit var fragmentView: View
    private lateinit var viewModel: HomeViewModel

    private lateinit var cancelBtn: MaterialButton
    private lateinit var saveBtn: MaterialButton
    private lateinit var resumeBtn: MaterialButton


    override fun onClick(v: View?) {
        Log.d(TAG, "onClick v: ${v.toString()}")
        when (v) {
            fragmentView.startButton -> {
                viewModel.startPressed()
                chronometerPersist.changeChronometerState(viewModel.currentLayoutState.ordinal)
                updateLayoutState()
            }
            fragmentView.editButton -> {
                Log.i(TAG, "EDIT")
            }
            fragmentView.cardView -> {
                Log.i(TAG, "CARD")
            }
            fragmentView.overviewBtn -> {
                Log.i(TAG, "OVERVIEW")
            }
            fragmentView.attendanceBtn -> {
                Log.i(TAG, "ATTENDANCE")
            }
            fragmentView.arc_progress -> {
                Log.i(TAG, "ARC")
            }
            cancelBtn -> {
                alertDialog.dismiss()
                viewModel.dialogCancel()
                chronometerPersist.changeChronometerState(viewModel.currentLayoutState.ordinal)
                updateLayoutState()
            }
            saveBtn -> {
                alertDialog.dismiss()
                viewModel.dialogSave()
                chronometerPersist.changeChronometerState(viewModel.currentLayoutState.ordinal)
                updateLayoutState()
            }
            resumeBtn -> {
                alertDialog.dismiss()
                viewModel.dialogResume()
                chronometerPersist.changeChronometerState(viewModel.currentLayoutState.ordinal)
                updateLayoutState()
            }
            else -> Log.e(TAG, "Something went wrong in the onClick")
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

        chronometerPersist = ChronometerPersist.getInstance(fragmentView.chrom, this)

        return fragmentView

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        updateLayoutState()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        chronometerPersist.resumeState()
    }

    private fun updateLayoutState() {
        fragmentView.startButton.background.setTint(resources.getColor(viewModel.startButtonColor, null))
        fragmentView.startButton.text = viewModel.startButtonText
        fragmentView.startTime.text = viewModel.startTimeString
        fragmentView.endTime.text = viewModel.endTimeString
        Log.d(TAG, "viewModel.currentLayoutState.ordinal: ${viewModel.currentLayoutState.name}")
        if (viewModel.currentLayoutState.ordinal == 2) showSaveDialog()
    }

    /*
    Mostly Placeholder
     */
    private fun showSaveDialog() {
        Log.d(TAG, "showSaveDialog")

        //Inflates View
        val layoutView = layoutInflater.inflate(R.layout.dialog_save, null)
        saveBtn = layoutView.findViewById(R.id.saveBtn)
        cancelBtn = layoutView.findViewById(R.id.cancelBtn)
        resumeBtn = layoutView.findViewById(R.id.resumeBtn)
        val text = layoutView.findViewById<TextView>(R.id.text)
        text.text = ((SystemClock.elapsedRealtime() - chronometerPersist.mChronometer.base) / 1000).toString()

        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(layoutView)

        alertDialog = dialogBuilder.create()
        alertDialog.setOnCancelListener {
            viewModel.dialogDismiss()
            chronometerPersist.changeChronometerState(viewModel.currentLayoutState.ordinal)
            updateLayoutState()
        }
        alertDialog.show()

        saveBtn.setOnClickListener(this)
        cancelBtn.setOnClickListener(this)
        resumeBtn.setOnClickListener(this)
    }

}
