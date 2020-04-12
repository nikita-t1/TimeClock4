package com.studio.timeclock4.fragments

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.studio.timeclock4.R
import com.studio.timeclock4.viewmodel.TimeSettingsViewModel
import com.studio.timeclock4.viewmodel.TimeSettingsViewModel.InputField
import kotlinx.android.synthetic.main.fragment_time_settings.*
import timber.log.Timber
import com.studio.timeclock4.utils.PreferenceHelper as Pref
import com.yarolegovich.mp.MaterialStandardPreference as MSP

class TimeSettingsFragment : Fragment(R.layout.fragment_time_settings), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {

    private val hourShort by lazy {  requireContext().resources.getString(R.string.hour_short)}
    private val days by lazy { requireContext().resources.getString(R.string.days)}
    val hsl  by lazy {hourShort.length}
    val dl by lazy {days.length}

    private val viewModel: TimeSettingsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(TimeSettingsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setChipState()
        setObservers()
        setListeners()
    }

    private fun setChipState() {
        mondayChip.isChecked = Pref.read(Pref.MONDAY_CHIP, true)
        tuesdayChip.isChecked = Pref.read(Pref.TUESDAY_CHIP, true)
        wednesdayChip.isChecked = Pref.read(Pref.WEDNESDAY_CHIP, true)
        thursdayChip.isChecked = Pref.read(Pref.THURSDAY_CHIP, true)
        fridayChip.isChecked = Pref.read(Pref.FRIDAY_CHIP, true)
        saturdayChip.isChecked = Pref.read(Pref.SATURDAY_CHIP, false)
        sundayChip.isChecked = Pref.read(Pref.SUNDAY_CHIP, false)

    }

    private fun setListeners() {
        working_time.setOnClickListener(this)
        working_time_week.setOnClickListener(this)
        pause_time.setOnClickListener(this)
        flex_account.setOnClickListener(this)
        vacation.setOnClickListener(this)

        mondayChip.setOnCheckedChangeListener(this)
        tuesdayChip.setOnCheckedChangeListener(this)
        wednesdayChip.setOnCheckedChangeListener(this)
        thursdayChip.setOnCheckedChangeListener(this)
        fridayChip.setOnCheckedChangeListener(this)
        saturdayChip.setOnCheckedChangeListener(this)
        sundayChip.setOnCheckedChangeListener(this)

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
            working_time -> showDialog(InputField.workingTime, (v as MSP), Pref.WORKING_TIME)
            working_time_week -> showDialog(InputField.workingTimeWeek, (v as MSP), Pref.WORKING_TIME_WEEK)
            pause_time -> showDialog(InputField.pauseTime, (v as MSP), Pref.PAUSE_TIME)
            flex_account -> showDialog(InputField.flexAccount, (v as MSP), Pref.FLEX_ACCOUNT)
            vacation -> showDialog(InputField.vacation, (v as MSP), Pref.VACATION)
        }
    }

    private fun showDialog(field: InputField, msp: com.yarolegovich.mp.MaterialStandardPreference, prefRead: String){
        TimeSettingsSheetFragment(field, msp, prefRead).show(childFragmentManager, tag)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView) {
            mondayChip -> viewModel.updateWorkWeek(Pref.MONDAY_CHIP, isChecked)
            tuesdayChip -> viewModel.updateWorkWeek(Pref.TUESDAY_CHIP, isChecked)
            wednesdayChip -> viewModel.updateWorkWeek(Pref.WEDNESDAY_CHIP, isChecked)
            thursdayChip -> viewModel.updateWorkWeek(Pref.THURSDAY_CHIP, isChecked)
            fridayChip -> viewModel.updateWorkWeek(Pref.FRIDAY_CHIP, isChecked)
            saturdayChip -> viewModel.updateWorkWeek(Pref.SATURDAY_CHIP, isChecked)
            sundayChip -> viewModel.updateWorkWeek(Pref.SUNDAY_CHIP, isChecked)
        }
    }
}
