package com.studio.timeclock4.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.studio.timeclock4.BuildConfig
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.AppIconHelperV26
import com.studio.timeclock4.viewmodel.AboutViewModel
import kotlinx.android.synthetic.main.alert_text_input_link.view.*
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
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

        iconView.clipToOutline = true
        viewModel.viewModelScope.launch {
            delay(100)
            linearLayout.visibility = View.VISIBLE
            playIconAnimatorSet()
        }

        setAppIcon()

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

    private fun setAppIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val bitmap = AppIconHelperV26.getAppIcon(requireContext(), BuildConfig.APPLICATION_ID)
            iconView.setImageBitmap(bitmap)
        } else {
            val dp = Pref.DEV_IconViewDp
            iconView.layoutParams.height = convertDpToPixel(dp)
            iconView.layoutParams.width = convertDpToPixel(dp)
            iconView.setImageResource(R.mipmap.ic_launcher)
        }
    }

    private fun playIconAnimatorSet() {
        val anim1 = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f)
        val anim2 = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.playTogether(anim1, anim2)
        animatorSet.start()
    }

    private fun convertDpToPixel(dp: Float): Int {
        val metrics: DisplayMetrics = resources.displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return px.roundToInt()
    }
}


