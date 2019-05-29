package com.studio.timeclock4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.studio.timeclock4.R
import com.studio.timeclock4.viewmodel.OptionsViewModel

class OptionsFragment : Fragment() {

    companion object {
        fun newInstance() = OptionsFragment()
    }

    private lateinit var viewModel: OptionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OptionsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
