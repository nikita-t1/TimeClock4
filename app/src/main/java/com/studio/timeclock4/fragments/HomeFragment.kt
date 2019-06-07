package com.studio.timeclock4.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.ChronometerPersist
import com.studio.timeclock4.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment(), View.OnClickListener {


    private lateinit var chronometerPersist: ChronometerPersist
    private val TAG = this.javaClass.simpleName
    private lateinit var fragmentView: View
    private lateinit var viewModel: HomeViewModel


    override fun onClick(v: View?) {
        when (v) {
            fragmentView.startButton -> {
                Log.i(TAG, "START")
                viewModel.startPressed()
                restoreLayout()
                if (viewModel.isStartPressed) chronometerPersist.startChronometer() else chronometerPersist.stopChronometer()

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
        restoreLayout()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        chronometerPersist.resumeState()
    }

    private fun restoreLayout() {
        fragmentView.startButton.background.setTint(resources.getColor(viewModel.startButtonColor, null))
        fragmentView.startButton.text = viewModel.startButtonText
    }

}
