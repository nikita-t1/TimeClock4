package com.studio.timeclock4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.CalendarUtils
import com.studio.timeclock4.utils.OnSwipeTouchListener
import com.studio.timeclock4.viewmodel.ListingViewModel
import kotlinx.android.synthetic.main.fragment_listing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import timber.log.Timber

class ListingOuterFragment : Fragment() {

    private var pagerAdapter: ListingFragmentAdapter? = null
    private val listingViewModel: ListingViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ListingViewModel::class.java)
    }
    private lateinit var viewPager: ViewPager2
    private lateinit var lottieAnimation: LottieAnimationView
//    private val itemCount1 = CalendarUtils.getWeeksBetween(CalendarUtils.startDate, CalendarUtils.endDate).toInt()
    // Precalculated
    private val itemCount1 = CalendarUtils.getWeeksBetween()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_listing, container, false)

        viewPager = view.findViewById(R.id.pager)
        lottieAnimation = view.findViewById(R.id.lottie_animation)
        lottieAnimation.apply {
            repeatCount = LottieDrawable.INFINITE
            setAnimation(R.raw.lottie_loading_circle)
            playAnimation()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ext_toolbar.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                listingViewModel.position.value?.plus(1)?.let {
                    viewPager.setCurrentItem(it, true)
                    listingViewModel.setPosition(it)
                }
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                listingViewModel.position
                listingViewModel.position.value?.minus(1)?.let {
                    viewPager.setCurrentItem(it, true)
                    listingViewModel.setPosition(it)
                }
            }
        })

        listingViewModel.viewModelScope.launch(Dispatchers.Main) {
            pagerAdapter = ListingFragmentAdapter()

            // Looks nicer when pushing the toolbar up
            delay(100)

            viewPager.apply {
                isUserInputEnabled = false
                adapter = pagerAdapter
                offscreenPageLimit = 1

                setCurrentItem(
                    CalendarUtils.getWeeksBetween(CalendarUtils.startDate, LocalDateTime.now()).toInt(),
                    false
                )
            }

            // TODO
            listingViewModel.position.observe(viewLifecycleOwner, Observer {
                val currentWeek = CalendarUtils.startDate.plusWeeks(it.toLong())

                val weekString = "${resources.getText(R.string.week)}"
                week_text.text = "$weekString ${CalendarUtils.getWeekOfYear(currentWeek)}"

                val startDate: String = "${currentWeek.dayOfMonth} ${currentWeek.month.toString().toLowerCase().capitalize()}"
                val endDate: String = " ${currentWeek.plusDays(6).dayOfMonth} ${currentWeek.plusDays(6).month.toString().toLowerCase().capitalize()}"
                val year: String = " ${currentWeek.year}"
                month_text.text = "$startDate - $endDate $year"

//            week_balance_current_text.text = ""
//            week_balance_remaining_text.text = ""
            })

            listingViewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
                if (!isLoading) {
                    viewPager.animate().apply {
                        alpha(1f)
//                        setDuration(300).interpolator = DecelerateInterpolator()
                    }
                    Timber.i("Data Loading Completed")
                    lottieAnimation.apply {
                        cancelAnimation()
                        visibility = View.GONE
                    }
                }
            })
        }
    }

    private inner class ListingFragmentAdapter : FragmentStateAdapter(this.activity!!) {

        override fun createFragment(position: Int): Fragment {
            return ListingInnerFragment(position)
        }

        override fun getItemCount(): Int {
            return itemCount1
        }
    }
}
