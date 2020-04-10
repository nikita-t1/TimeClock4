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
import com.studio.timeclock4.utils.PreferenceHelper as Pref
import com.studio.timeclock4.viewmodel.MainSettingsViewModel
import kotlinx.android.synthetic.main.fragment_main_settings.*

class MainSettingsFragment : Fragment(R.layout.fragment_main_settings), View.OnClickListener {

    private lateinit var fragmentView: View
    private val viewModel: MainSettingsViewModel by lazy { ViewModelProvider(this).get(MainSettingsViewModel::class.java) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rebirth_btn.setOnClickListener(this)
        saving_swt.setOnClickListener(this)
        frames_swt.setOnClickListener(this)
        anim_swt.setOnClickListener(this)
        error_btn.setOnClickListener(this)

        saving_swt.isChecked = Pref.read(Pref.DEV_EnableSaving, false)
        frames_swt.isChecked = Pref.read(Pref.DEV_EnableFrames, true)
        anim_swt.isChecked = Pref.read(Pref.DEV_EnableDayButtonAnimation, true)
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

        }
    }
}
