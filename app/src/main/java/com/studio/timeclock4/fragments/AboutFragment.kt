package com.studio.timeclock4.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.studio.timeclock4.R
import com.studio.timeclock4.viewmodel.AboutViewModel
import kotlinx.android.synthetic.main.alert_text_input_link.view.*
import kotlinx.android.synthetic.main.fragment_about.*
import com.studio.timeclock4.utils.PreferenceHelper as Pref

class AboutFragment : Fragment(R.layout.fragment_about) {

    private val viewModel: AboutViewModel by lazy {
        ViewModelProvider(this).get(AboutViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update_btn.setSummary(Pref.read(Pref.UPDATE_LINK, Pref.Default_UPDATE_LINK))

        val viewInflated = LayoutInflater.from(context)
            .inflate(R.layout.alert_text_input_link, getView() as ViewGroup?, false)
        viewInflated.input.setText(Pref.read(Pref.UPDATE_LINK, Pref.Default_UPDATE_LINK))

        update_btn.setOnClickListener(){
            val builder = AlertDialog.Builder(context).apply {
                setTitle("UpdateLink")
                setView(viewInflated)
                setPositiveButton(R.string.add) { dialog, which ->
                    Pref.write(Pref.UPDATE_LINK, viewInflated.input.text.toString())
                    update_btn.setSummary(Pref.read(Pref.UPDATE_LINK, Pref.Default_UPDATE_LINK))
                    dialog.dismiss()
                }
                show()
            }
        }
    }
}
