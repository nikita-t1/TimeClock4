package com.studio.timeclock4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.studio.timeclock4.BuildConfig
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.BottomSheetFragment
import com.studio.timeclock4.utils.BottomSheetStyle
import com.studio.timeclock4.utils.BottomSheetTheme
import com.studio.timeclock4.viewmodel.OptionsViewModel
import kotlinx.android.synthetic.main.fragment_options.*

class OptionsFragment : Fragment() {

    private lateinit var fragmentView: View
    private val viewModel: OptionsViewModel by lazy{ ViewModelProvider(this).get(OptionsViewModel::class.java)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_options, container, false)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        materialStandardPreference6.apply {
            setTitle("${resources.getString(R.string.about)} ${resources.getString(R.string.app_name_final)}")
            setSummary(BuildConfig.VERSION_NAME)
            materialStandardPreference2.setOnClickListener(){
                Navigation.findNavController(view).navigate(R.id.action_destination_options_to_timeSettingsFragment)
            }

            setOnClickListener(){
                AboutFragment()
                Navigation.findNavController(view).navigate(R.id.destination_about)
            }

            setOnLongClickListener{
                if (BuildConfig.DEBUG){
                    Navigation.findNavController(view).navigate(R.id.destination_settings)
                }else {
                    val i = resources.openRawResource(R.raw.todo).bufferedReader().use { it.readText() }
                    BottomSheetFragment("Herzlichen Glückwunsch,\n du hast das versteckte Menü entdeckt\n",
                        i, arrayListOf(), BottomSheetStyle.BOTTOM_SHEET, BottomSheetTheme.LIGHT
                    ).show(childFragmentManager, "")
                }
                return@setOnLongClickListener true
            }
        }
    }
}

