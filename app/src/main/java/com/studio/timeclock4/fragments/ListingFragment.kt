package com.studio.timeclock4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.studio.timeclock4.R
import com.studio.timeclock4.viewmodel.ListingViewModel

class ListingFragment : Fragment() {

    companion object {
        fun newInstance() = ListingFragment()
    }

    private lateinit var viewModel: ListingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_listing, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ListingViewModel::class.java)
        // TODO: Use the ViewModel
    }
}