package com.studio.timeclock4.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.android.material.button.MaterialButton
import com.studio.timeclock4.R
import com.studio.timeclock4.model.WorkDay
import com.studio.timeclock4.utils.CalendarUtils
import com.studio.timeclock4.utils.PreferenceHelper
import com.studio.timeclock4.utils.TimeCalculations.convertMinutesToDateString
import com.studio.timeclock4.viewmodel.ListingViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.action_add_workday.*
import kotlinx.android.synthetic.main.viewpager_page.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import timber.log.Timber

class ListingInnerFragment(private val position: Int) : Fragment(), View.OnClickListener {

    private val currentWeekMondayDate by lazy { CalendarUtils.startDate.plusWeeks(position.toLong())}
    private var lastClickedButton: MaterialButton? = null
    private lateinit var layoutView: View
    private var elevation: Float = 0.0f
    private val listingViewModel: ListingViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ListingViewModel::class.java)
    }
    private val addWorkdayCard: View by lazy{
        layoutView.findViewById<ViewStub>(R.id.viewStub).inflate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.viewpager_page, container, false)
        return layoutView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        elevation  = monday_btn.elevation
        setOnClickListeners()
        createAnimatorSet(0f).start()
    }

    private fun setOnClickListeners() {
        monday_btn.setOnClickListener(this)
        tuesday_btn.setOnClickListener(this)
        wednesday_btn.setOnClickListener(this)
        thursday_btn.setOnClickListener(this)
        friday_btn.setOnClickListener(this)
        saturday_btn.setOnClickListener(this)
        sunday_btn.setOnClickListener(this)
        edit_btn.setOnClickListener(this)
    }

    private fun simulateClick() {
        if (CalendarUtils.getWeeksBetween(currentWeekMondayDate, LocalDateTime.now()) == (0L)){
            when (LocalDateTime.now().dayOfWeek){
                DayOfWeek.MONDAY -> monday_btn.callOnClick()
                DayOfWeek.TUESDAY -> tuesday_btn.callOnClick()
                DayOfWeek.WEDNESDAY -> wednesday_btn.callOnClick()
                DayOfWeek.THURSDAY -> thursday_btn.callOnClick()
                DayOfWeek.FRIDAY -> friday_btn.callOnClick()
                DayOfWeek.SATURDAY -> saturday_btn.callOnClick()
                DayOfWeek.SUNDAY -> sunday_btn.callOnClick()
                else -> monday_btn.callOnClick()
            }
        } else monday_btn.callOnClick()
    }

    override fun onResume() {
        Timber.i("RESUME")
        listingViewModel.setPosition(position)
        listingViewModel.loading.observeOnce(viewLifecycleOwner, Observer {isLoading ->
            if (!isLoading){
                simulateClick()
                (0 until ext_week.childCount).forEach {
                    val materialButton = ext_week.getChildAt(it) as MaterialButton
                    materialButton.setTextColor(listingViewModel.workDayCheck(dayToInt(materialButton.text.toString()).toLong()))
                }
            }
        })
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        createAnimatorSet(0f).start()
        Toasty.info(requireContext(), "PAUSE", Toasty.LENGTH_SHORT).show()
        lastClickedButton?.apply {
            animate().translationY(0f).start()
            elevation= this@ListingInnerFragment.elevation
        }
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                if (!listingViewModel.loading.value!!){
                    removeObserver(this)
                }
            }
        })
    }


    private fun dayToInt(day: String): Int{
        return when (day) {
            resources.getString(R.string.monday_short) ->  1
            resources.getString(R.string.tuesday_short) -> 2
            resources.getString(R.string.wednesday_short) -> 3
            resources.getString(R.string.thursday_short) -> 4
            resources.getString(R.string.friday_short) -> 5
            resources.getString(R.string.saturday_short) -> 6
            resources.getString(R.string.sunday_short) -> 7
            else -> 0
        }
    }

    override fun onClick(v: View?) {
        when (v){
            edit_btn -> {
                Timber.i("Edit \n DAY ${dayToInt(lastClickedButton?.text.toString())}")
                listingViewModel.setEditDialog(true)
                val dialog = EditFragment(listingViewModel.weekDayToViewModelElement(DayOfWeek.of(dayToInt(lastClickedButton!!.text.toString()))).value!!, EditFragment.DatabaseAction.UPDATE)
                dialog.show(childFragmentManager, dialog.tag)
            }

            else -> {
                Timber.i("CLICKEDDD")
                val clickedButton = v as MaterialButton
                val currentDate =
                    (currentWeekMondayDate.plusDays(dayToInt(clickedButton.text.toString()).toLong() - 1)) //Everything alright -1
                createAnimatorSet(0f).start()
                lastClickedButton?.apply {
                    animate().translationY(0f).start()
                    elevation= this@ListingInnerFragment.elevation
                }

                listingViewModel.viewModelScope.launch(Dispatchers.Main) {

                    //Looks nice with the fade ObjectAnimator

                    if (PreferenceHelper.read(PreferenceHelper.DEV_EnableDayButtonAnimation, false)){
                        delay(PreferenceHelper.DEV_DefaultDayButtonAnimationTime)
                    }

                    val workday = listingViewModel.weekDayToViewModelElement(currentDate.dayOfWeek)

                    date_full_text.text = "${currentDate.dayOfMonth}. ${currentDate.month.toString().capitalise()} ${currentDate.year}"
                    day_text.text = currentDate.dayOfWeek.toString().capitalise()
                    day_balance_current_text.text = convertMinutesToDateString(workday.value!!.workTimeGross.toLong())
                    day_balance_remaining_text.text = convertMinutesToDateString(workday.value!!.overtime.toLong())
                    starttime_text.text = convertMinutesToDateString(workday.value!!.timeClockIn.toLong())
                    endtime_text.text = convertMinutesToDateString(workday.value!!.timeClockOut.toLong())
                    breaktime_text.text = convertMinutesToDateString(workday.value!!.pauseTime.toLong())
                    note_text.text = workday.value!!.userNote

                    clickedButton.apply {
                        elevation = 0f
                        animate()
                            .translationY(28f)
                            .start()
                    }
                    delay(PreferenceHelper.DEV_DefaultExtDayRevealDelay) // Delay bis ext_day zu sehen ist
                    createAnimatorSet(1f).start()

                    if (workday.value == listingViewModel.emptyWorkDay) {
                        ext_day.children.forEach { child ->
                            child.isEnabled = false
                        }
                        addWorkdayCard.visibility = View.VISIBLE
                        addDay_btn.setOnClickListener() {
                            listingViewModel.setEditDialog(true)
                            val dialog = EditFragment(
                                WorkDay(
                                    0, currentDate.year, CalendarUtils.getWeekOfYear(currentDate), currentDate.dayOfWeek.value,
                                    currentDate.dayOfMonth, currentDate.monthValue, 0, 0, 0, 0,
                                    0, 0, true, null, null, null
                                ), EditFragment.DatabaseAction.INSERT
                            )
                            dialog.show(childFragmentManager, dialog.tag)
                        }
                    } else {
                        ext_day.children.forEach { child ->
                            child.isEnabled = true
                        }
                        addWorkdayCard.visibility = View.GONE
                    }

                    lastClickedButton = clickedButton
                }
            }
        }
    }

    private fun createAnimatorSet(alpha: Float): AnimatorSet {
        val extView_fade = ObjectAnimator.ofFloat(ext_day, View.ALPHA, alpha)
        val viewStub_fade = ObjectAnimator.ofFloat(addWorkdayCard, View.ALPHA, alpha)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(extView_fade, viewStub_fade)
        return animatorSet
    }
}

private fun String.capitalise(): CharSequence? {
    return this.toLowerCase().capitalize()
}
