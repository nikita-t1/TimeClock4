package com.studio.timeclock4.fragments

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.CalendarUtils
import com.studio.timeclock4.utils.PreferenceHelper
import com.studio.timeclock4.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home_details.*

class HomeDetailsFragment(private val sourceY: Int, private val bottomNavBarHeight: Int) : DialogFragment() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.attributes?.windowAnimations = R.style.HomeDetailsFragmentAnimation
        return inflater.inflate(R.layout.fragment_home_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.startTimeString.observe(viewLifecycleOwner, Observer { startTime.text = it })
        viewModel.endTimeString.observe(viewLifecycleOwner, Observer { endTime.text = it })
        viewModel.pauseTimeString.observe(viewLifecycleOwner, Observer { pauseTime.text = it })
        viewModel.progressBarDay.observe(viewLifecycleOwner, Observer {
            circularProgressView.setPercentage((it * 3.6).toInt()) // 100% == 360° Degree
            circularProgressView.setStepCountText("$it%")
        })
        viewModel.remainingText.observe(viewLifecycleOwner, Observer {
            val remainingString =
                viewModel.progressBarDay.value?.let { progressBarDayValue ->
                    if (progressBarDayValue >= 100) {
                        remainingTextDay.setTextColor(resources.getColor(R.color.green, null))
                        return@let "+ $it"
                    } else {
                        remainingTextDay.setTextColor(resources.getColor(R.color.red, null))
                        return@let "- $it"
                    }
                }
            remainingTextDay.text = remainingString
        })
        edit_btn.setOnClickListener {
            val dialog = EditFragment(
                viewModel.createTemporaryWorkDay(),
                EditFragment.DatabaseAction.PREVIEW
            )
            showEditDialog(dialog)
        }
    }

    override fun onResume() {
        super.onResume()
        dayStringFull.text = CalendarUtils.getFullDateString()
        val window = dialog!!.window ?: return
        val params = window.attributes

        params.height = resources.displayMetrics.heightPixels - sourceY - bottomNavBarHeight
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.y = bottomNavBarHeight + dpToPx(PreferenceHelper.DEV_HomeDetailsFragmentMarginBottom)
        params.dimAmount = PreferenceHelper.DEV_HomeDetailsFragmentDimAmount

        window.setGravity(Gravity.BOTTOM)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.attributes = params
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val frag: HomeFragment? = targetFragment as HomeFragment?
        frag?.onDetailsDismiss()
    }

    private fun showEditDialog(dialog: DialogFragment) {
        dialog.setTargetFragment(targetFragment, 0)
        dialog.show(parentFragmentManager, "dialog")
    }

    private fun dpToPx(valueInDp: Float): Int {
        val metrics = activity!!.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics).toInt()
    }
}
