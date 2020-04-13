package com.studio.timeclock4.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.studio.timeclock4.MainActivity
import com.studio.timeclock4.R
import com.studio.timeclock4.viewmodel.MainSettingsViewModel
import kotlinx.android.synthetic.main.fragment_main_settings.*
import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog
import com.studio.timeclock4.utils.PreferenceHelper as Pref


class MainSettingsFragment : Fragment(R.layout.fragment_main_settings), View.OnClickListener{

    private lateinit var fragmentView: View
    private val viewModel: MainSettingsViewModel by lazy {
        ViewModelProvider(this).get(MainSettingsViewModel::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rebirth_btn.setOnClickListener(this)
        saving_swt.setOnClickListener(this)
        frames_swt.setOnClickListener(this)
        anim_swt.setOnClickListener(this)
        error_btn.setOnClickListener(this)
        titleColorPicker.setOnClickListener(this)
        radio_first.setOnClickListener(this)
        radio_second.setOnClickListener(this)
        leakCanaryCheckBox.setOnClickListener(this)

        titleColorPicker.setIconColor(Pref.read(Pref.DEV_TitleColor, 0))
        if (Pref.read(Pref.DEV_ColorTitle_U, false)) {
            radio_first.isChecked = true
        } else {
            radio_second.isChecked = true
        }
        saving_swt.isChecked = Pref.read(Pref.DEV_EnableSaving, false)
        frames_swt.isChecked = Pref.read(Pref.DEV_EnableFrames, true)
        anim_swt.isChecked = Pref.read(Pref.DEV_EnableDayButtonAnimation, true)
        leakCanaryCheckBox.isChecked = leakcanary.LeakCanary.config.dumpHeap
    }

    private fun triggerRebirth(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        if (context is Activity) context.finish()
        Runtime.getRuntime().exit(0)
    }

    override fun onClick(v: View?) {
        when (v) {
            rebirth_btn -> triggerRebirth(requireContext())

            saving_swt -> Pref.write(Pref.DEV_EnableSaving, saving_swt.isChecked)
            frames_swt -> Pref.write(Pref.DEV_EnableFrames, frames_swt.isChecked)
            anim_swt -> Pref.write(Pref.DEV_EnableDayButtonAnimation, anim_swt.isChecked)

            error_btn -> error("Error Triggered Thought Debug Settings")

            titleColorPicker -> {
                ColorPickerDialog()
                    .withAlphaEnabled(false)
                    .withColor(Pref.read(Pref.DEV_TitleColor, 0))
                    .withListener { dialog, color ->
                        Pref.write(Pref.DEV_TitleColor, color)
                        titleColorPicker.setIconColor(color)
                        // a color has been picked; use it
                    }
                    .show(parentFragmentManager, "colorPicker")
            }
            radio_first -> Pref.write(Pref.DEV_ColorTitle_U, true)
            radio_second -> Pref.write(Pref.DEV_ColorTitle_U, false)
            leakCanaryCheckBox -> leakcanary.LeakCanary.config =
                leakcanary.LeakCanary.config.copy(dumpHeap = leakCanaryCheckBox.isChecked)

        }
    }
}
