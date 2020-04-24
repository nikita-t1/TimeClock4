package com.studio.timeclock4.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import timber.log.Timber

object AppIconHelperV26 {
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getAppIcon(mContext: Context, packageName: String?): Bitmap? {
        try {
            val drawable = mContext.packageManager.getApplicationIcon(packageName)
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            } else if (drawable is AdaptiveIconDrawable) {
                val backgroundDr = drawable.background
                val foregroundDr = drawable.foreground

                val drr = arrayOfNulls<Drawable>(2)
                drr[0] = backgroundDr
                drr[1] = foregroundDr

                val layerDrawable = LayerDrawable(drr)

                val width = layerDrawable.intrinsicWidth
                val height = layerDrawable.intrinsicHeight

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)

                layerDrawable.setBounds(0, 0, canvas.width, canvas.height)
                layerDrawable.draw(canvas)

                val roundedBitmapDrawable: RoundedBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(mContext.resources, bitmap)
                val roundPx = width.toFloat() * PreferenceHelper.DEV_IconViewV26RoundPx
                Timber.e("PIXEL $roundPx")
                roundedBitmapDrawable.cornerRadius = roundPx

                return roundedBitmapDrawable.toBitmap(width, height)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
}