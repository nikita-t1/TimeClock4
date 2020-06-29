package com.studio.timeclock4.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.AppUpdaterUtils.UpdateListener
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.studio.timeclock4.BuildConfig
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.AppIconHelperV26
import com.studio.timeclock4.viewmodel.AboutViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.roundToInt
import com.studio.timeclock4.utils.PreferenceHelper as Pref

class AboutFragment : Fragment(R.layout.fragment_about) {

    private var appUpdaterUtils: AppUpdaterUtils? = null
    private val viewModel: AboutViewModel by lazy {
        ViewModelProvider(this).get(AboutViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        versionText.text = "${resources.getString(R.string.version)} ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        copyrightNotice.text = "${resources.getString(R.string.copyright_notice)} ${resources.getString(R.string.company)}"

        iconView.clipToOutline = true
        viewModel.viewModelScope.launch {
            delay(Pref.DEV_IconVisibilityDelay.toLong())
            linearLayout.visibility = View.VISIBLE
            playIconAnimatorSet()
        }
        setAppIcon()

        update_btn.setOnClickListener() {
            if (appUpdaterUtils == null) {
                appUpdaterUtils =
                    AppUpdaterUtils(requireContext())
                        .setUpdateFrom(UpdateFrom.JSON)
                        .setUpdateJSON(Pref.DEV_UpdateLink)
                        .withListener(object : UpdateListener {
                            override fun onSuccess(update: Update, isUpdateAvailable: Boolean?) {
                                if (isUpdateAvailable!!) {
                                    Toasty.info(requireContext(), resources.getString(R.string.update_found), Toasty.LENGTH_SHORT).show()
                                } else {
                                    Toasty.info(requireContext(), resources.getString(R.string.no_update_found), Toasty.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailed(error: AppUpdaterError?) {
                                Timber.e("Something went wrong here")
                            }
                        })
            }
            appUpdaterUtils?.start()
        }
    }

    override fun onDetach() {
        super.onDetach()
        appUpdaterUtils?.stop()
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
