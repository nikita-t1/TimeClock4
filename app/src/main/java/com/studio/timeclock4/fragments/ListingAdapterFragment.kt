package com.studio.timeclock4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.studio.timeclock4.R
import com.studio.timeclock4.viewmodel.ListingViewModel
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import org.threeten.extra.YearWeek

class ListingAdapterFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private val listingViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ListingViewModel::class.java)
    }

    private lateinit var firstPageDate: OffsetDateTime
    private val monthsBeforeInstall: Long = 2 // How far you can scroll back from installdate in months

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_listing_adapter, container, false)
        viewPager = view.findViewById(R.id.viewpager_listing)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstPageDate = listingViewModel.installDate.minusMonths(monthsBeforeInstall)
        val mondayOfFirstPageDate =
            firstPageDate.minusDays(firstPageDate.dayOfWeek.value - 1L)
        val amountPages =
            ChronoUnit.WEEKS.between(
                mondayOfFirstPageDate, OffsetDateTime.now().plusWeeks(1)
            ).toInt()

        val pagerAdapter = ViewPagerAdapter()
        pagerAdapter.itemCount = amountPages
        viewPager.apply {
            adapter = pagerAdapter
            offscreenPageLimit = 1
            setCurrentItem((amountPages), false)
        }
        pagerAdapter.itemCount++ // Initializes next Page

        // Adds new Page when you are on the last one
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == pagerAdapter.itemCount - 1) {
                    pagerAdapter.itemCount++
                }
            }
        })
    }

    private inner class ViewPagerAdapter : FragmentStateAdapter(requireActivity()) {

        private var mItemCount: Int = 0

        override fun getItemCount(): Int {
            return mItemCount
        }

        fun setItemCount(itemCount: Int) {
            mItemCount = itemCount
            notifyDataSetChanged()
        }

        override fun createFragment(position: Int): Fragment {
            val yearWeek = YearWeek.from(firstPageDate.plusWeeks(position.toLong()))
            return ListingPageFragment(yearWeek)
        }
    }
}
