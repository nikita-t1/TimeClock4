package com.studio.timeclock4.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.studio.timeclock4.R
import com.studio.timeclock4.viewmodel.TimeSettingsViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_time_settings.*
import timber.log.Timber
import com.studio.timeclock4.utils.PreferenceHelper as Pref
import com.studio.timeclock4.viewmodel.TimeSettingsViewModel.InputField as InputField
import com.yarolegovich.mp.MaterialStandardPreference as MSP

class TimeSettingsFragment : Fragment(R.layout.fragment_time_settings), View.OnClickListener {

    private val hourShort by lazy {  requireContext().resources.getString(R.string.hour_short)}
    private val days by lazy { requireContext().resources.getString(R.string.days)}
    val hsl  by lazy {hourShort.length}
    val dl by lazy {days.length}

    private val viewModel: TimeSettingsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(TimeSettingsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setListeners()
        Toasty.info(requireContext(), "Angaben in Dezimal", Toasty.LENGTH_SHORT).show()
    }

    private fun setListeners() {
        working_time.setOnClickListener(this)
        working_time_week.setOnClickListener(this)
        pause_time.setOnClickListener(this)
        flex_account.setOnClickListener(this)
        vacation.setOnClickListener(this)
    }

    private fun setObservers() {
        viewModel.workingTime.observe(viewLifecycleOwner, Observer {
            working_time.setSummary(it)
        })
        viewModel.workingTimeWeek.observe(viewLifecycleOwner, Observer {
            working_time_week.setSummary(it)
        })
        viewModel.pauseTime.observe(viewLifecycleOwner, Observer {
            pause_time.setSummary(it)
        })
        viewModel.flexAccount.observe(viewLifecycleOwner, Observer {
            flex_account.setSummary(it)
        })
        viewModel.vacation.observe(viewLifecycleOwner, Observer {
            vacation.setSummary(it)
        })
    }

    override fun onClick(v: View?) {
        Timber.w("CLICK")

        when (v){
            working_time -> showDialog(InputField.workingTime, (v as MSP), Pref.working_time)
            working_time_week -> showDialog(InputField.workingTimeWeek, (v as MSP), Pref.working_time_week)
            pause_time -> showDialog(InputField.pauseTime, (v as MSP), Pref.pause_time)
            flex_account -> showDialog(InputField.flexAccount, (v as MSP), Pref.flex_account)
            vacation -> showDialog(InputField.vacation, (v as MSP), Pref.vacation)
        }
    }

    private fun showDialog(field: InputField, msp: com.yarolegovich.mp.MaterialStandardPreference, prefRead: String){
        TimeSettingsSheetFragment(field, msp, prefRead).show(childFragmentManager, tag)
    }
}
