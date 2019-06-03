package com.studio.timeclock4.fragments

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.studio.timeclock4.R
import com.studio.timeclock4.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment(), View.OnClickListener {

    val TAG = this.javaClass.simpleName
    lateinit var view1: View

    override fun onClick(v: View?) {
        when (v) {
            view1.startButton -> {
                Log.i(TAG, "START PRESSED")
            }
        }

        Toast.makeText(requireContext(), "Button Clicked", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "VIew ${v.toString()} $v")
    }

    private lateinit var chronometer: Chronometer

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view1 = inflater.inflate(R.layout.fragment_home, container, false)
        view1.startButton.setOnClickListener(this)
        chronometer = Chronometer(requireContext())
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
        Log.i(TAG, (SystemClock.elapsedRealtime() - chronometer.base).toString());
        chronometer.setOnChronometerTickListener { chronometer ->
            Log.i(TAG, (SystemClock.elapsedRealtime() - chronometer.base).toString());
        }
        return view1
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
