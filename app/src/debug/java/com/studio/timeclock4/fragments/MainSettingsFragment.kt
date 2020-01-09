package com.studio.timeclock4.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.studio.timeclock4.MainActivity
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.PreferenceHelper
import com.studio.timeclock4.viewmodel.MainSettingsViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_main_settings.*
import timber.log.Timber

class MainSettingsFragment : Fragment(R.layout.fragment_main_settings){

    private lateinit var fragmentView: View
    private val viewModel: MainSettingsViewModel by lazy { ViewModelProvider(this).get(MainSettingsViewModel::class.java) }


//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        rebirth_btn.setOnClickListener(this)
//        saving_swt.setOnClickListener(this)
//        db_recreation_swt.setOnClickListener(this)
//        frames_swt.setOnClickListener(this)
//        anim_swt.setOnClickListener(this)
//
//        name_edit_text.setText("Google is your friendddddddddddd.", TextView.BufferType.EDITABLE)
//
//        Timber.w(saving_swt.isChecked.toString() + " ü")
//        saving_swt.isChecked = PreferenceHelper.read("enable saving", false)
//        db_recreation_swt.isChecked = PreferenceHelper.read("enable database recreation", false)
//        frames_swt.isChecked = PreferenceHelper.read("enable frames", true)
//        anim_swt.isChecked = PreferenceHelper.read("dayButton anim", true)
//        Timber.w(saving_swt.isChecked.toString() + " ü")
//    }
//
//    private fun triggerRebirth(context: Context) {
//        val intent = Intent(context, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        context.startActivity(intent)
//        if (context is Activity) {
//            (context as Activity).finish()
//        }
//        Runtime.getRuntime().exit(0)
//    }
//
//    override fun onClick(v: View?) {
//        when (v) {
//            rebirth_btn -> triggerRebirth(requireContext())
//
//            saving_swt -> PreferenceHelper.write("enable saving", saving_swt.isChecked)
//            db_recreation_swt -> PreferenceHelper.write("enable database recreation", db_recreation_swt.isChecked)
//            frames_swt -> {
//                Toasty.info(requireContext(), frames_swt.isChecked.toString(), Toasty.LENGTH_SHORT).show()
//                PreferenceHelper.write("enable frames", frames_swt.isChecked)
//
//            }
//            anim_swt -> PreferenceHelper.write("dayButton anim", anim_swt.isChecked)
//
//        }
//    }
}
