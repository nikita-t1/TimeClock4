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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.ChronometerPersist
import com.studio.timeclock4.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment(), View.OnClickListener {

    private val TAG = this.javaClass.simpleName
    private lateinit var alertDialog: AlertDialog
    private lateinit var chronometerPersist: ChronometerPersist
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
                chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
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
                chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
            }
            saveBtn -> {
                alertDialog.dismiss()
                viewModel.dialogSave()
                chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
            }
            resumeBtn -> {
                alertDialog.dismiss()
                viewModel.dialogResume()
                chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
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
        chronometerPersist.hourFormat(true)

//        chronometerPersist.mChronometer.setOnChronometerTickListener {
//            fragmentView.arc_progress.progress = viewModel.getArcProgress(((SystemClock.elapsedRealtime() - chronometerPersist.mChronometer.base) / 1000))
//        }


        return fragmentView

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        lifecycle.addObserver(viewModel);


        viewModel.startButtonText.observe(this, Observer { fragmentView.startButton.text = it })
        viewModel.startTimeString.observe(this, Observer { fragmentView.startTime.text = it })
        viewModel.endTimeString.observe(this, Observer { fragmentView.endTime.text = it })
        viewModel.currentLayoutStateOrdinal.observe(this, Observer {
            if (it.ordinal == 2) showSaveDialog()
        })
        viewModel.startButtonColor.observe(this, Observer {
            fragmentView.startButton.background.setTint(
                resources.getColor(it, null)
            )
        })
    }

    override fun onResume() {
        super.onResume()
        chronometerPersist.resumeState()
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
            chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
        }
        alertDialog.show()

        saveBtn.setOnClickListener(this)
        cancelBtn.setOnClickListener(this)
        resumeBtn.setOnClickListener(this)
    }

}
