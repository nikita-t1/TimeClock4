package com.studio.timeclock4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studio.timeclock4.R
import com.studio.timeclock4.WorkWeekAdapter
import com.studio.timeclock4.viewmodel.ListingViewModel

class ListingFragment : Fragment() {

    private lateinit var listingViewModel: ListingViewModel

    companion object {
        fun newInstance() = ListingFragment()
    }

    private lateinit var viewModel: ListingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_listing, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerPage)
        val adapter = WorkWeekAdapter(requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Get a new or existing ViewModel from the ViewModelProvider.
        listingViewModel = ViewModelProvider(this).get(ListingViewModel::class.java)
        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        listingViewModel.allWorkDays.observe(this, Observer { workDays ->
            workDays.let { adapter.setWorkDays(it) }
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ListingViewModel::class.java)
        // TODO: Use the ViewModel
    }
}