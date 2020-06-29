package com.studio.timeclock4.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.*
import com.studio.timeclock4.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber

class HomeFragment : Fragment(R.layout.fragment_home), View.OnClickListener {

    private lateinit var chronometerPersist: ChronometerPersist
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    }
    private val bottomNavBarHeight by lazy {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56f, activity!!.resources.displayMetrics).toInt()
    }

    override fun onClick(v: View?) {
        when (v) {
            startButton -> {
                viewModel.startPressed()
                chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
            }
            cardView -> {
                val location = IntArray(2)
                guidelineHorizontalCenter.getLocationOnScreen(location)
                val sourceX = location[0]
                val sourceY = location[1]
                Timber.i("$sourceY, ${resources.displayMetrics.heightPixels}, $bottomNavBarHeight")
                createAnimatorSet(0f).start()
                showEditDialog(HomeDetailsFragment(sourceY, bottomNavBarHeight))
            }
            attendanceBtn -> {
                Timber.i("ATTENDANCE")
            }
            else -> Timber.e("Something went wrong in the onClick")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(viewModel)

        startButton.setOnClickListener(this)
        cardView.setOnClickListener(this)
        attendanceBtn.setOnClickListener(this)

        chronometerPersist = ChronometerPersist.getInstance(chrom)
        chrom.setOnChronometerTickListener {
            val elapsedMillis = (SystemClock.elapsedRealtime() - it.base)
            viewModel.onChronometerClick(elapsedMillis, true)
            Timber.i("${(elapsedMillis / 1000 / 60)}")
        }
        dayStringFull.text = CalendarUtils.getFullDateString()

        viewModel.startButtonText.observe(viewLifecycleOwner, Observer { startButton.text = it })
        viewModel.startTimeString.observe(viewLifecycleOwner, Observer { startTime.text = it })
        viewModel.endTimeString.observe(viewLifecycleOwner, Observer { endTime.text = it })
        viewModel.currentLayoutStateOrdinal.observe(viewLifecycleOwner, Observer {
            if (it.ordinal == 2) showSaveDialog()
//            editButton.isClickable = it.ordinal == 1
        })
        viewModel.startButtonColor.observe(viewLifecycleOwner, Observer {
            startButton.background.setTint(
                resources.getColor(it, null)
            )
        })
        viewModel.chronometerFormat.observe(viewLifecycleOwner, Observer {
            chrom.format = it
        })
        viewModel.progressBarDay.observe(viewLifecycleOwner, Observer {
            horizontal_progress_bar.progress = it
            Timber.i("$it")
        })
        viewModel.progressBarWeek.observe(viewLifecycleOwner, Observer {
            week_progress_view.setPercentage(kotlin.math.floor(it * 3.6).toInt())
            week_progress_view.setStepCountText("$it%")
        })
        viewModel.remainingText.observe(viewLifecycleOwner, Observer {
            val remainingString =
                viewModel.progressBarDay.value?.let { progressBarDayValue ->
                    if (progressBarDayValue >= 100) {
                        remainingText.setTextColor(resources.getColor(R.color.green, null))
                        return@let "+ $it"
                    } else {
                        remainingText.setTextColor(resources.getColor(R.color.red, null))
                        return@let "- $it"
                    }
                }
            remainingText.text = remainingString
        })

        viewModel.onChronometerClick(1, true)
    }

    private fun showEditDialog(dialog: DialogFragment) {
        dialog.setTargetFragment(this, 0)
        dialog.show(parentFragmentManager, "dialog")
    }

    override fun onResume() {
        super.onResume()
        chronometerPersist.resumeState()
        Timber.i("IM BACK BITCHES")
    }

    private fun createAnimatorSet(alpha: Float): AnimatorSet {
        val progressBar_fade = ObjectAnimator.ofFloat(horizontal_progress_bar, View.ALPHA, alpha)
        val remainingText_fade = ObjectAnimator.ofFloat(remainingText, View.ALPHA, alpha)
        val cardViewConstraint_fade = ObjectAnimator.ofFloat(cardViewConstraint, View.ALPHA, alpha)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(progressBar_fade, remainingText_fade, cardViewConstraint_fade)
        return animatorSet
    }

    private fun showSaveDialog() {
        val time = ((SystemClock.elapsedRealtime() - chronometerPersist.mChronometer.base) / 1000 / 60)
        val timeString = TimeCalculations.convertMinutesToDateString(time)

        val alert = AlertView(timeString, resources.getString(R.string.clock_out_question), BottomSheetStyle.BOTTOM_SHEET)
        alert.addAction(AlertAction(resources.getString(R.string.clock_out), BottomSheetActionStyle.POSITIVE) {
            viewModel.dialogSave(time)
            chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
        })
        alert.addAction(AlertAction(resources.getString(R.string.resume), BottomSheetActionStyle.DEFAULT) {
            viewModel.dialogResume()
            chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
        })
        alert.addAction(AlertAction(resources.getString(R.string.abort), BottomSheetActionStyle.NEGATIVE) {
            viewModel.dialogCancel()
            chronometerPersist.changeChronometerState(viewModel.currentLayoutStateOrdinal.value!!.ordinal)
        })
        alert.show(childFragmentManager)
    }

    fun receiveNewWorkDayValues(startTimeMin: Int, pauseTime: Int, userNote: String) {
        val currentStartTime = PreferenceHelper.read(PreferenceHelper.CURRENT_START_TIME, PreferenceHelper.Default_START_TIME)
        if (currentStartTime < startTimeMin) {
            val difference = startTimeMin - currentStartTime
            chronometerPersist.substractFromChronometerBase(difference * 60)
        } else if (currentStartTime > startTimeMin) {
            val difference = currentStartTime - startTimeMin
            chronometerPersist.addToChronometerBase(difference * 60)
        }
        viewModel.setNewWorkDayValues(startTimeMin, pauseTime, userNote)
    }

    fun onDetailsDismiss() {
        createAnimatorSet(1f).start()
    }
}
