package com.studio.timeclock4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.studio.timeclock4.R
import com.studio.timeclock4.viewmodel.TimeSettingsViewModel
import com.yarolegovich.mp.MaterialStandardPreference
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_time_settings_sheet.view.*

class TimeSettingsSheetFragment(val field: TimeSettingsViewModel.InputField, val msp: MaterialStandardPreference, val prefRead: String) : BottomSheetDialogFragment(){

    private val viewModel: TimeSettingsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(TimeSettingsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_time_settings_sheet, container, false)
        view.textInputEditText.requestFocus()
        view.textInputLayout.hint = msp.title
        view.textInputEditText.setOnEditorActionListener { v, actionId, event ->
        val array = viewModel.verifyText(field, view.textInputEditText.text.toString())
            if (array[1] == false){
                Toasty.info(requireContext(), "Ausserhalb des gültigen Bereiches", Toasty.LENGTH_SHORT).show()
            }else {
                viewModel.updateField(field, (array[0] as Int).toLong())
            }

            dismiss()
            return@setOnEditorActionListener true

        }
        return view
    }
}