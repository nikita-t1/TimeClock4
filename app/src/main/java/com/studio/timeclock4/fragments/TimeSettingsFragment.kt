package com.studio.timeclock4

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.studio.timeclock4.debug.R

class TimeSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = TimeSettingsFragment()
    }

    private lateinit var viewModel: TimeSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.time_settings_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TimeSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
