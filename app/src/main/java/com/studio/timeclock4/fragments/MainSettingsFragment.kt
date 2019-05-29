package com.studio.timeclock4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.studio.timeclock4.R
import com.studio.timeclock4.viewmodel.MainSettingsViewModel

class MainSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = MainSettingsFragment()
    }

    private lateinit var viewModel: MainSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
